package com.sushant.RateLimiter.ratelimit.config;

public record PlanConfig(
        long capacity,
        long refillRate,
        long windowSizeInSec,
        RateLimiterType defaultAlgo
) {
}
