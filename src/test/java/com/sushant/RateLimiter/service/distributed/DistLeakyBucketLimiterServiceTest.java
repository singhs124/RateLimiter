//package com.sushant.RateLimiter.service.distributed;
//
//import com.sushant.RateLimiter.ratelimit.algorithm.distributed.DistLeakyBucketLimiterService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.utility.DockerImageName;
//
//import java.util.Objects;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class DistLeakyBucketLimiterServiceTest {
//    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
//            .withExposedPorts(6379);
//    static {
//        redis.start();
//        System.setProperty("spring.data.redis.host", redis.getHost());
//        System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString());
//    }
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    private DistLeakyBucketLimiterService rateLimiter;
//
//    @BeforeEach
//    void setUp(){
//        rateLimiter = new DistLeakyBucketLimiterService(redisTemplate);
//        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().flushAll();
//    }
//
//    @Test
//    void shouldAllowConsumptionWhenBucketHasSpace(){
//        boolean allowed = rateLimiter.tryConsume("test:allow-1",1);
//        assertTrue(allowed);
//        long available = rateLimiter.getAvailableTokens("test:user-1");
//        assertEquals(4,available);
//    }
//
//    @Test
//    void shouldBlockWhenConsumptionIsExceeded(){
//        String id = "test:block-1";
//        for(int i = 0 ; i < 5 ; i++){
//            rateLimiter.tryConsume(id,1);
//        }
//        boolean blocked = rateLimiter.tryConsume(id,1);
//        assertFalse(blocked);
//    }
//
//    @Test
//    void shouldAllowRequestAfterLeaking() throws InterruptedException {
//        String id = "test:leak-1";
//        for(int i = 0 ; i < 5; i++) rateLimiter.tryConsume(id,1);
//        Thread.sleep(1100);
//        assertTrue(rateLimiter.tryConsume(id,1));
//    }
//}
