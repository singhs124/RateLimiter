package com.sushant.RateLimiter.auth.dto;

public record AuthTokenResDTO(String access_token, String refresh_token, long created_at) {
}
