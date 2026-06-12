package com.sushant.RateLimiter.ratelimit.service;


import com.sushant.RateLimiter.ratelimit.algorithm.RateLimiter;
import com.sushant.RateLimiter.ratelimit.config.PlanConfig;
import com.sushant.RateLimiter.ratelimit.config.PlanConfigRegistry;
import com.sushant.RateLimiter.ratelimit.config.RateLimiterRegistry;
import com.sushant.RateLimiter.ratelimit.config.RateLimiterType;
import com.sushant.RateLimiter.ratelimit.dto.LuaResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private final PlanConfigRegistry planConfigRegistry;
    private final RateLimiterRegistry registry;

    public LuaResult allowRequest(String ownerId, String ratePlan, String clientId, RateLimiterType algoOverride, long tokens){
        PlanConfig config = planConfigRegistry.get(ratePlan);
        RateLimiter limiter = registry.resolve(algoOverride);
        String identifier = buildIdentifier(ownerId,clientId);
        log.debug("Rate Limit Check: identifier={} , plan={}, algorithm={}", identifier, ratePlan, algoOverride);
        return limiter.tryConsume(identifier, tokens,config);
    }

    private String buildIdentifier(String ownerId, String clientId){
        if(clientId == null || clientId.isBlank()) return ownerId;
        return ownerId + ":" + clientId;
    }

}
