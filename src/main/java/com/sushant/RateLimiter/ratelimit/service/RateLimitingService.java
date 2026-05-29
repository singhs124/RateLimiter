package com.sushant.RateLimiter.ratelimit.service;

import com.sushant.RateLimiter.ratelimit.algorithm.FixedWindowLimiterService;
import com.sushant.RateLimiter.ratelimit.algorithm.LeakyBucketLimiterService;
import com.sushant.RateLimiter.ratelimit.algorithm.RateLimiter;
import com.sushant.RateLimiter.ratelimit.algorithm.TokenBucketLimiterService;
import com.sushant.RateLimiter.ratelimit.config.PlanConfig;
import com.sushant.RateLimiter.ratelimit.config.PlanConfigRegistry;
import com.sushant.RateLimiter.ratelimit.config.RateLimiterType;
import com.sushant.RateLimiter.ratelimit.algorithm.distributed.DistLeakyBucketLimiterService;
import com.sushant.RateLimiter.ratelimit.algorithm.distributed.DistTokenBucketLimitierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitingService {

//    @Value("${rate.limiter.bucketCapacity}")
//    private long capacity;
//    @Value("${rate.limiter.refillRate}")
//    private long refillRate;
//    @Value("${rate.limiter.windowSizeInSec}")
//    private long windowSizeInSec;
//    @Value("${rate.limiter.type}")
    private RateLimiterType limiterType;

    private RateLimiter rateLimiter;

    private final StringRedisTemplate stringRedisTemplate;
    private final PlanConfigRegistry planConfigRegistry;

//    private final DistTokenBucketLimitierService distTokenBucketLimitierService;
//    private final DistLeakyBucketLimiterService leakyBucketLimiterService;

//    @PostConstruct
//    public void init(){
//        rateLimiter = createRateLimiter();
//        log.debug("RateLimiter Intialized {} with capacity: {}, rate: {} and window-size: {}", limiterType, capacity, refillRate, windowSizeInSec);
//    }

    private RateLimiter createRateLimiter(RateLimiterType type, PlanConfig config){
        long capacity = config.capacity();
        long refillRate = config.refillRate();
        long windowSizeInSec = config.windowSizeInSec();
        return switch (type){
            case TOKEN_BUCKET ->  new TokenBucketLimiterService(capacity,refillRate);
            case LEAKY_BUCKET -> new LeakyBucketLimiterService(capacity,refillRate);
            case FIXED_WINDOW_COUNTER -> new FixedWindowLimiterService(capacity,windowSizeInSec);
            case DIST_TOKEN_BUCKET ->  new DistTokenBucketLimitierService(stringRedisTemplate,capacity,refillRate);
            case DIST_LEAKY_BUCKET -> new DistLeakyBucketLimiterService(stringRedisTemplate,capacity,refillRate);
            default -> throw new IllegalArgumentException("Unsupported Limiter Type: "+ limiterType);
        };
    }

    public boolean allowRequest(String clientId, String ratePlan, long tokens){
        PlanConfig config = planConfigRegistry.get(ratePlan);
        RateLimiter limiter = createRateLimiter(config.defaultAlgo(), config);
        log.debug("Rate Limit Check: client id={} , plan={}, algorithm={}", clientId, ratePlan, config.defaultAlgo());
        return limiter.tryConsume(clientId, tokens);
    }

    public boolean allowRequest(String clientId, String ratePlan, RateLimiterType algoOverride,long tokens){
        PlanConfig config = planConfigRegistry.get(ratePlan);
        RateLimiter limiter = createRateLimiter(algoOverride, config);
        log.debug("Rate Limit Check: client id={} , plan={}, algorithm={}", clientId, ratePlan, algoOverride);
        return limiter.tryConsume(clientId, tokens);
    }
    //Todo: Make limiter Object on unified location.
    public long getAvailableTokens(String clientId, String ratePlan){
        PlanConfig config = planConfigRegistry.get(ratePlan);
        RateLimiter limiter = createRateLimiter(config.defaultAlgo(),config);
        return limiter.getAvailableTokens(clientId);
    }

    public long getAvailableTokens(String clientId, String ratePlan, RateLimiterType algoOverride){
        PlanConfig config = planConfigRegistry.get(ratePlan);
        RateLimiter limiter = createRateLimiter(algoOverride,config);
        return limiter.getAvailableTokens(clientId);
    }
}
