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
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DistTokenBucketLimitierService implements RateLimiter {
    private DefaultRedisScript<List> luaScript;

    private final StringRedisTemplate redisTemplate;

    public DistTokenBucketLimitierService(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init(){
        luaScript = new DefaultRedisScript<>();
        luaScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/token_bucket.lua")));
        luaScript.setResultType(List.class);
        log.info("🟢 LuaScript LOADED: {}", luaScript.getScriptAsString() != null ? "SUCCESS" : "FAILED");
    }

    @Override
    public RateLimiterType getType(){
        return RateLimiterType.DIST_TOKEN_BUCKET;
    }

    @Override
    public LuaResult tryConsume(String identifier, long tokens, PlanConfig planConfig) {
        return execute(identifier,planConfig,true);
    }

    public long getAvailableTokens(String identifier, PlanConfig planConfig) {
        return execute(identifier,planConfig,false).remaining();
    }

    private LuaResult execute(String identifier, PlanConfig planConfig, boolean isConsuming){
        long capacity = planConfig.capacity(), refillRate = planConfig.refillRate();
        String redisKey = "bucket:" + identifier;
        long currentTime = Instant.now().toEpochMilli();
        double refillRatePerSecond = refillRate/60.0 ;
        try{
            List result = redisTemplate.execute(
                    luaScript,
                    Collections.singletonList(redisKey),
                    String.valueOf(capacity),
                    String.valueOf(refillRatePerSecond),
                    String.valueOf(currentTime),
                    isConsuming ? "1":"0"
            );
            boolean allowed = ((Number)result.get(0)).longValue() == 1;
            long remaining = ((Number)result.get(1)).longValue();
            log.debug("Redis Token Bucket - {}: {} (available: {})",
                    identifier, allowed ? "ALLOWED" : "BLOCKED", remaining);
            return new LuaResult(allowed, remaining);
        } catch (Exception e){
            log.error("Redis error for {}, fail-open", identifier, e);
            throw new RuntimeException("Redis Error"); //todo: Make it right.
        }
    }
}
