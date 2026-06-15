package com.sushant.RateLimiter.infra.dto;

public record JwtUserPrincipal(String userId, String email, String plan) {
}
