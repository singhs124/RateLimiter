package com.sushant.RateLimiter.infra.config;

import com.sushant.RateLimiter.auth.dto.ApiKeyDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(factory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        log.debug("redisTemplate Bean Created!");
//        return redisTemplate;
//    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        log.debug("string Redis Template Bean Created!");
        return new StringRedisTemplate(factory);
    }

    @Bean
    public RedisTemplate<String, ApiKeyDTO> apiKeyRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, ApiKeyDTO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        JacksonJsonRedisSerializer<ApiKeyDTO> serializer = new JacksonJsonRedisSerializer<>(
                ApiKeyDTO.class);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
