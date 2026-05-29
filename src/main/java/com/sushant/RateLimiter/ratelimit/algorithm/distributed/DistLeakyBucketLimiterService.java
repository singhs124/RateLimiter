package com.sushant.RateLimiter.ratelimit.algorithm.distributed;

import com.sushant.RateLimiter.ratelimit.algorithm.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class DistLeakyBucketLimiterService implements RateLimiter {
    private DefaultRedisScript<List> luaScript;

    private final long capacity;
    private final long leakRate;
    private final StringRedisTemplate redisTemplate;

    public DistLeakyBucketLimiterService(StringRedisTemplate redisTemplate, long capacity, long leakRate){
        this.redisTemplate = redisTemplate;
        this.capacity = capacity;
        this.leakRate = leakRate;
        initLuaScript();
    }

    public void initLuaScript(){
        luaScript = new DefaultRedisScript<>();
        luaScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/leaky_bucket.lua")));
        luaScript.setResultType(List.class);
        log.info("🟢 LuaScript LOADED: {}", luaScript.getScriptAsString() != null ? "SUCCESS" : "FAILED");
    }

    @Override
    public boolean tryConsume(String identifier, long tokens) {
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
                    "1"
            );
            if(result == null || result.size()<2){
                log.error("Lua Script returned invalid result for {}", identifier);
                return true;
            }
            long allowedFlag = ((Number)result.get(0)).longValue();
            long availableTokens = ((Number)result.get(1)).longValue();
            boolean allowed = allowedFlag == 1;
            log.debug("Redis Leaky Bucket - {}: {} (available: {})",
                    identifier, allowed ? "ALLOWED" : "BLOCKED", availableTokens);
            return allowed;
        } catch (Exception e) {
            log.error("Redis error for {}, fail-open", identifier, e);
            return true;
        }
    }

    @Override
    public long getAvailableTokens(String identifier) {
        String bucketKey = "bucket:" + identifier ;
        String queueKey = "bucket:" + identifier +":q";
        long currentTime = Instant.now().toEpochMilli();
        double leakyRatePerSec = leakRate/60.0;
        try{
            List<String > keys = Arrays.asList(bucketKey,queueKey);
            List<?> result = redisTemplate.execute(
                    luaScript,
                    keys,
                    String.valueOf(capacity),
                    String.valueOf(leakyRatePerSec),
                    String.valueOf(currentTime),
                    "0"
            );
            if(result == null || result.size()<2){
                return 0;
            }
            long availableTokens = ((Number)result.get(1)).longValue();
            log.debug("Redis getAvailableTokens - {}: {}", identifier, availableTokens);
            return Math.max(availableTokens,0);
        } catch (Exception e) {
            log.error("Redis error for {}, fail-open", identifier, e);
            return 0;
        }
    }
}