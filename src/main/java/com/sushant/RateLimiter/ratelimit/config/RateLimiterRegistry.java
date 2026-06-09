package com.sushant.RateLimiter.ratelimit.config;

import com.sushant.RateLimiter.ratelimit.algorithm.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RateLimiterRegistry {

    private final Map<RateLimiterType, RateLimiter> registry;

    public RateLimiterRegistry(List<RateLimiter> rateLimiters){
        registry = new HashMap<>();
        for (RateLimiter rl: rateLimiters){
            registry.put(rl.getType(),rl);
        }
        log.info("✅ Registered algorithms: {}", registry.keySet());
    }

    public RateLimiter resolve(RateLimiterType rateLimiterType){
        RateLimiter rateLimiter = registry.get(rateLimiterType);
        if(rateLimiter == null){
            throw new UnsupportedOperationException("No RateLimiter Registered for type "+ rateLimiterType);
        }
        return rateLimiter;
    }

}
