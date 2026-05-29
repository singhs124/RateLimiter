package com.sushant.RateLimiter.ratelimit.algorithm.distributed;

import com.sushant.RateLimiter.ratelimit.algorithm.RateLimiter;
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

//@Service
@Slf4j
//@RequiredArgsConstructor
public class DistTokenBucketLimitierService implements RateLimiter {
    private DefaultRedisScript<List> luaScript;

    private final long capacity;
    private final long refillRate;
    private final StringRedisTemplate redisTemplate;

    public DistTokenBucketLimitierService(StringRedisTemplate redisTemplate, long capacity, long refillRate){
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.redisTemplate = redisTemplate;
        initLua();
    }

//    public DistTokenBucketLimitierService(StringRedisTemplate redisTemplate){
//        this.redisTemplate = redisTemplate;
//    }


//    @PostConstruct
    public void initLua(){
        log.info("🟢 @PostConstruct init() STARTING");
        luaScript = new DefaultRedisScript<>();
        luaScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/token_bucket.lua")));
        luaScript.setResultType(List.class);
        log.info("🟢 LuaScript LOADED: {}", luaScript.getScriptAsString() != null ? "SUCCESS" : "FAILED");
    }

    @Override
    public boolean tryConsume(String identifier, long tokens) {
        String redisKey = "bucket:" + identifier;
//        String redisKey = "bucket:user123";
        long currentTime = Instant.now().toEpochMilli();
        double refillRatePerSecond = refillRate/60.0 ;
        try{
            List result = redisTemplate.execute(
                    luaScript,
                    Collections.singletonList(redisKey),
                    String.valueOf(capacity),
                    String.valueOf(refillRatePerSecond),
                    String.valueOf(currentTime),
                    "1"
            );
            boolean allowed = ((Number)result.get(0)).longValue() == 1;
            long availableTokens = ((Number)result.get(1)).longValue();
//            boolean allowed = availableTokens >= 1;
            log.debug("Redis Token Bucket - {}: {} (available: {})",
                    identifier, allowed ? "ALLOWED" : "BLOCKED", availableTokens);
            return allowed;
        } catch (Exception e){
            log.error("Redis error for {}, fail-open", identifier, e);
            return true;
        }
    }


    @Override
    public long getAvailableTokens(String identifier) {
        String redisKey = "bucket:" + identifier;
//        String redisKey = "bucket:user123";
        long currentTime = Instant.now().toEpochMilli();
        double refillRatePerSecond = refillRate/60.0 ;
        try{
            List result = redisTemplate.execute(
                    luaScript,
                    Collections.singletonList(redisKey),
                    String.valueOf(capacity),
                    String.valueOf(refillRatePerSecond),
                    String.valueOf(currentTime),
                    "0"
            );
            long available = ((Number)result.get(1)).longValue();
//            long availableTokens = ((Number)result.get(1)).longValue();
            log.debug("Redis getAvailableTokens - {}: {}", identifier, available);
            return Math.max(0, available);
        } catch (Exception e){
            log.error("Redis error for {}, return 0", identifier, e);
            return 0;
        }
    }
}
