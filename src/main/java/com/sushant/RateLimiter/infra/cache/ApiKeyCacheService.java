package com.sushant.RateLimiter.infra.cache;

import com.sushant.RateLimiter.infra.config.Constants;
import com.sushant.RateLimiter.auth.entity.ApiKeyStatus;
import com.sushant.RateLimiter.auth.dto.ApiKeyDTO;
import com.sushant.RateLimiter.auth.entity.UserApiKey;
import com.sushant.RateLimiter.auth.repo.UserAPIRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class ApiKeyCacheService {
    private static final String redisKeyPrefix = Constants.REDIS_KEY_PREFIX;
    private static final Duration redisTTL = Constants.REDIS_TTL;

    private final RedisTemplate<String, ApiKeyDTO> redisTemplate;
    private final CacheManager cacheManager;
    private final UserAPIRepository userAPIRepository;

//Todo: Reports Spring @Qualifier annotations on class fields that are ignored by the corresponding
//  Lombok @RequiredArgsConstructor and @AllArgsConstructor annotations. The generated constructors
//  will not receive the @Qualifier annotation without a lombok. copyableAnnotations definition
//  inside the lombok. config file. [Qualifier for cacheManager]

    public ApiKeyCacheService(RedisTemplate<String, ApiKeyDTO> redisTemplate,
                              @Qualifier("caffeineCacheManager") CacheManager cacheManager,
                              UserAPIRepository userAPIRepository){
        this.redisTemplate = redisTemplate;
        this.cacheManager = cacheManager;
        this.userAPIRepository = userAPIRepository;
    }

    public Optional<ApiKeyDTO> lookup(String uuid){
        Cache caffeineCache = cacheManager.getCache(Constants.API_KEY_CACHE);
        Cache.ValueWrapper cached = caffeineCache.get(uuid);
        if(cached != null){
            return Optional.ofNullable((ApiKeyDTO) cached.get());
        }

        String redisKey = redisKeyPrefix + uuid;
        ApiKeyDTO fromRedis = redisTemplate.opsForValue().get(redisKey);
        if(fromRedis != null){
            caffeineCache.put(uuid,fromRedis);
            return Optional.of(fromRedis);
        }
        return Optional.empty();
    }

    public void evict(String uuid){
        Cache caffeineCache = cacheManager.getCache(Constants.API_KEY_CACHE);
        caffeineCache.evict(uuid);
        redisTemplate.delete(Constants.REDIS_KEY_PREFIX+uuid);
    }

    public void populate(String uuid, ApiKeyDTO data) {
        Cache caffineCache = cacheManager.getCache(Constants.API_KEY_CACHE);
        caffineCache.put(uuid,data);
        redisTemplate.opsForValue().set(Constants.REDIS_KEY_PREFIX+uuid, data, Constants.REDIS_TTL);
    }

    public ApiKeyDTO mapToDto(UserApiKey entity, String uuid){
        Boolean isRevoked = entity.getStatus() == ApiKeyStatus.REVOKED;
        return new ApiKeyDTO(
                uuid,
                entity.getKeyHashed(),
                entity.getPlanType().toString(),
                isRevoked
        );
    }


}
