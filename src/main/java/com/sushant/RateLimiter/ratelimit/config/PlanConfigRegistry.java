package com.sushant.RateLimiter.ratelimit.config;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PlanConfigRegistry {
    private static final Map<String, PlanConfig> PLANS = Map.of(
            "FREE", new PlanConfig(10,1,60, RateLimiterType.DIST_LEAKY_BUCKET),
            "PRO", new PlanConfig(100,5,60, RateLimiterType.DIST_LEAKY_BUCKET),
            "ENTERPRISE", new PlanConfig(1000,20,60, RateLimiterType.DIST_LEAKY_BUCKET)
    );

    public PlanConfig get(String ratePlan) {
        return PLANS.getOrDefault(ratePlan, PLANS.get("FREE"));
    }

}
