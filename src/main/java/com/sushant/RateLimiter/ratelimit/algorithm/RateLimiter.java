package com.sushant.RateLimiter.ratelimit.algorithm;

public interface RateLimiter {
    public boolean tryConsume(String identifier, long tokens);
    public long getAvailableTokens(String identifier);
}
