package com.sushant.RateLimiter.ratelimit.config;

public enum RateLimiterType {
    TOKEN_BUCKET,
    LEAKY_BUCKET,
    FIXED_WINDOW_COUNTER,
    DIST_TOKEN_BUCKET,
    DIST_LEAKY_BUCKET
}
