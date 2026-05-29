package com.sushant.RateLimiter.ratelimit.dto;

public record RateLimitResult(Boolean allowed, Long remaining) {
}
