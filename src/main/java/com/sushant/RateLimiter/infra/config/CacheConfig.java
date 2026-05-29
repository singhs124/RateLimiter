package com.sushant.RateLimiter.infra.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.sushant.RateLimiter.common.util.Constants;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    private static final String API_KEY_CACHE = Constants.API_KEY_CACHE;

    @Bean(name = "caffeineCacheManager")
    @Primary
    public CacheManager cacheManager(){
        CaffeineCacheManager manager = new CaffeineCacheManager(API_KEY_CACHE);
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .recordStats()
        );
        return manager;
    }

//    Todo: We can use this Bean to remove Spring Abstraction to reduce Latency.
//    @Bean
//    public com.github.benmanes.caffeine.cache.Cache<String, ApiKeyDTO> localCache() {
//        return Caffeine.newBuilder()
//                .maximumSize(5000)
//                .expireAfterWrite(30, TimeUnit.SECONDS)
//                .build();
//    }
//FOR SERVICE CHANGES IF USING ABOVE BEAN
//    @Autowired
//    private com.github.benmanes.caffeine.cache.Cache<String, ApiKeyDTO> localCache;
//
//    public ApiKeyDTO lookup(String uuid) {
//        // 1. Local Cache (Caffeine) - Super fast RAM access
//        ApiKeyDTO data = localCache.getIfPresent(uuid);
//        if (data != null) return data;
//
//        // 2. Redis Lookup
//        // ... your redis code ...
//
//        // 3. Put back in local
//        if (foundData != null) localCache.put(uuid, foundData);
//        return foundData;
//    }
}