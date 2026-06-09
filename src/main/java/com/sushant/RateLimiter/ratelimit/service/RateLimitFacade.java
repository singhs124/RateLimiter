//package com.sushant.RateLimiter.ratelimit.service;
//
//import com.sushant.RateLimiter.ratelimit.config.RateLimiterType;
//import com.sushant.RateLimiter.ratelimit.dto.RateLimitResult;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RateLimitFacade {
//    private final RateLimitingService rateLimitingService;
//
//    public RateLimitResult evaluate(String clientId, String ratePlan, RateLimiterType algo){
//        boolean allowed = rateLimitingService.allowRequest(clientId,ratePlan,algo,1L);
//        long remaining = rateLimitingService.getAvailableTokens(clientId,ratePlan,algo);
//        return new RateLimitResult(allowed,remaining);
//    }
//}
