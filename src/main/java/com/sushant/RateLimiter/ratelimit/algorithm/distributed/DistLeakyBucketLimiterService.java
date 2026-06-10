package com.sushant.RateLimiter.ratelimit.algorithm.distributed;

import com.sushant.RateLimiter.ratelimit.algorithm.RateLimiter;
import com.sushant.RateLimiter.ratelimit.config.PlanConfig;
import com.sushant.RateLimiter.ratelimit.config.RateLimiterType;
import com.sushant.RateLimiter.ratelimit.dto.LuaResult;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class DistLeakyBucketLimiterService implements RateLimiter {
    private DefaultRedisScript<List> luaScript;

    private final StringRedisTemplate redisTemplate;

    public DistLeakyBucketLimiterService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init(){
        luaScript = new DefaultRedisScript<>();
        luaScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/leaky_bucket.lua")));
        luaScript.setResultType(List.class);
        log.info("🟢 LuaScript LOADED: {}", luaScript.getScriptAsString() != null ? "SUCCESS" : "FAILED");
    }

    @Override
    public RateLimiterType getType(){
        return RateLimiterType.DIST_LEAKY_BUCKET;
    }

    @Override
    public LuaResult tryConsume(String identifier, long tokens, PlanConfig planConfig) {
        return execute(identifier,planConfig,true);
    }


    public long getAvailableTokens(String identifier, PlanConfig planConfig) {
        return execute(identifier, planConfig, false).remaining();
    }

    private LuaResult execute(String identifier, PlanConfig config, boolean isConsuming){
        long leakRate = config.refillRate(), capacity = config.capacity();
        String bucketKey = "bucket:" + identifier ;
        String queueKey = "bucket:" + identifier +":q";
        long currentTime = Instant.now().toEpochMilli();
        double leakyRatePerSec = leakRate/60.0;
        try{
            List<String> keys = Arrays.asList(bucketKey,queueKey);
            List<?> result = redisTemplate.execute(
                    luaScript,
                    keys,
                    String.valueOf(capacity),
                    String.valueOf(leakyRatePerSec),
                    String.valueOf(currentTime),
                    isConsuming ? "1":"0"
            );
            if(result == null || result.size()<2){
                log.error("Lua Script returned invalid result for {}", identifier);
                return new LuaResult(true, 0L);
            }
            long allowedFlag = ((Number)result.get(0)).longValue();
            long availableTokens = ((Number)result.get(1)).longValue();
            boolean allowed = allowedFlag == 1;
            log.debug("Redis Leaky Bucket - {}: {} (available: {})",
                    identifier, allowed ? "ALLOWED" : "BLOCKED", availableTokens);
            return new LuaResult(allowed, availableTokens);
        } catch (Exception e) {
            log.error("Redis error for {}, fail-open", identifier, e);
            throw new RuntimeException("Redis Error"); //todo: Make it right.
        }

    }
}