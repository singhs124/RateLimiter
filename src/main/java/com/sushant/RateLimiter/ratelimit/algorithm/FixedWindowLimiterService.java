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
//public class FixedWindowLimiterService implements RateLimiter {
//    public static class Window {
//        private final long windowSizeMs;
//        private final long maxReq;
//        private AtomicLong windowStart;
//        private AtomicLong currentTokenCounts; //tokens already taken away
//
//        public Window(long windowSizeMs, long capacity){
//            this.windowSizeMs = windowSizeMs;
//            this.currentTokenCounts = new AtomicLong(0);
//            this.windowStart = new AtomicLong(Instant.now().toEpochMilli());
//            this.maxReq = capacity;
//        }
//
//        public synchronized boolean tryConsume(long tokens){
//            long currentTime = Instant.now().toEpochMilli();
//            long currentWindowStart = windowStart.get();
//            long elapsedTime = currentTime - currentWindowStart;
//
//            if(elapsedTime >= windowSizeMs){
//                currentTokenCounts.set(0);
//                windowStart.set(currentTime);
//            }
//            if(currentTokenCounts.get() + tokens <= maxReq){
//                currentTokenCounts.addAndGet(tokens);
//                return true;
//            }
//            return false;
//        }
//
//        public synchronized long getAvailableTokens(){
//            return maxReq -currentTokenCounts.get();
//        }
//    }
//    private final Map<String, Window> buckets = new ConcurrentHashMap<>();
//    private final long windowSizeInMs;
//    private final long capacity;
//
//    public FixedWindowLimiterService(long capacity, long windowSize){
//        this.windowSizeInMs = windowSize*1000;
//        this.capacity = capacity;
//    }
//
//    @Override
//    public boolean tryConsume(String identifier, long tokens) {
//        Window bucket = buckets.computeIfAbsent(
//                identifier,
//                k->new Window(windowSizeInMs,capacity)
//        );
//        boolean allowed = bucket.tryConsume(tokens);
//        log.debug("Fixed Window Counter - {}: {} (available: {})",
//                identifier, allowed ? "ALLOWED" : "BLOCKED", bucket.getAvailableTokens());
//        return allowed;
//    }
//
//    @Override
//    public long getAvailableTokens(String identifier) {
//        Window bucket = buckets.get(identifier);
//        long available = bucket.getAvailableTokens();
//        if(available <= 0) return 0;
//        return available;
//    }
//}
//
