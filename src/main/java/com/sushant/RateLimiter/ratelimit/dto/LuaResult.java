package com.sushant.RateLimiter.ratelimit.dto;

public record LuaResult(boolean allowed, long remaining) {
}
