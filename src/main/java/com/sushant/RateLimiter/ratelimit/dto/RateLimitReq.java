package com.sushant.RateLimiter.ratelimit.dto;

import com.sushant.RateLimiter.ratelimit.config.RateLimiterType;

public record RateLimitReq(String clientId, RateLimiterType algo) {
}

