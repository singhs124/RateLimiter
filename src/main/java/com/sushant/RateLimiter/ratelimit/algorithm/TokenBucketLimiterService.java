//package com.sushant.RateLimiter.ratelimit.algorithm;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.time.Instant;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Slf4j
//public class TokenBucketLimiterService implements RateLimiter {
//    public static class Bucket{
//        private final long maxBucketSize;
//        private final double refillRate;
//        private final AtomicLong availableTokens;
//        private final AtomicLong lastRefillTimestamp;
//
//        public Bucket(long maxBucketSize, double refillRate){
//            this.maxBucketSize = maxBucketSize;
//            this.refillRate = refillRate;
//            this.availableTokens = new AtomicLong(maxBucketSize);
//            this.lastRefillTimestamp = new AtomicLong(Instant.now().toEpochMilli());
//        }
//
//        public synchronized void refill(){
//            long currentTime = Instant.now().toEpochMilli();
//            long elapsedTime = currentTime - lastRefillTimestamp.get();
//
//            double tokensToAdd = (elapsedTime*refillRate)/1000;
//            if(tokensToAdd>0){
//                long tokenToAddFloor = (long) Math.floor(tokensToAdd);
//                long currentTokenCount = availableTokens.get();
//                long newTokenCount = Math.min(maxBucketSize, currentTokenCount+tokenToAddFloor);
//                if(newTokenCount>currentTokenCount){
//                    availableTokens.set(newTokenCount);
//                    lastRefillTimestamp.set(currentTime);
//                }
//            }
//        }
//
//        public synchronized boolean tryConsume(long tokens){
//            refill();
//            if(availableTokens.get()>=tokens){
//                availableTokens.addAndGet(-tokens);
//                return true;
//            }
//            return false;
//        }
//
//        public synchronized long getAvailableTokens(){
//            refill();
//            return availableTokens.get();
//        }
//    }
//
//    private final Map<String , Bucket> buckets = new ConcurrentHashMap<>();
//    private final long capacity;
//    private final long refillRate;
//
//    public TokenBucketLimiterService(long capacity, long refillRate){
//        this.capacity = capacity;
//        this.refillRate = refillRate;
//    }
//
//    @Override
//    public boolean tryConsume(String identifier, long tokens) {
//        Bucket bucket = buckets.computeIfAbsent(
//                identifier,
//                k->new Bucket(capacity,refillRate/60.0)
//        );
//        boolean allowed = bucket.tryConsume(1);
//        log.debug("Token Bucket - {}: {} (available: {})",
//                identifier, allowed ? "ALLOWED" : "BLOCKED", bucket.getAvailableTokens());
//        return allowed;
//    }
//
//    @Override
//    public long getAvailableTokens(String identifier) {
//        Bucket bucket = buckets.get(identifier);
//        long available = bucket.getAvailableTokens();
//        if(available<=0) return 0;
//        return available;
//    }
//}
