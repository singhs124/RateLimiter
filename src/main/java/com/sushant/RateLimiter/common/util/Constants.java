package com.sushant.RateLimiter.common.util;

import java.time.Duration;

public class Constants {
    public static final String API_KEY_CACHE = "apiKeys";
    public static final String REDIS_KEY_PREFIX = "apikey:";
    public static final Duration REDIS_TTL = Duration.ofMinutes(60);
}