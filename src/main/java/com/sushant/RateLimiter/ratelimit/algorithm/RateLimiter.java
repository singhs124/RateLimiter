package com.sushant.RateLimiter.ratelimit.algorithm;

import com.sushant.RateLimiter.ratelimit.config.PlanConfig;
import com.sushant.RateLimiter.ratelimit.config.RateLimiterType;
import com.sushant.RateLimiter.ratelimit.dto.LuaResult;

public interface RateLimiter {
    RateLimiterType getType();
    public LuaResult tryConsume(String identifier, long tokens, PlanConfig planConfig);
}
