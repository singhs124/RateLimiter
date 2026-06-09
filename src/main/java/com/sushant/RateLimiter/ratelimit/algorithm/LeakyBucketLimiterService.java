//package com.sushant.RateLimiter.ratelimit.algorithm;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.time.Instant;
//import java.util.LinkedList;
//import java.util.Map;
//import java.util.Queue;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Slf4j
//public class LeakyBucketLimiterService implements RateLimiter {
//    public static class Bucket{
//        private final Queue<Long> queue = new LinkedList<>();
//        private final long capacity;
//        private final double leakRatePerSecond;
//        private AtomicLong lastLeakTime;
//
//        public Bucket(long capacity, double leakRatePerSecond){
//            this.capacity = capacity;
//            this.leakRatePerSecond = leakRatePerSecond;
//            this.lastLeakTime = new AtomicLong(Instant.now().toEpochMilli());
//        }
//
//        public synchronized void leak(){
//            long currentTime = Instant.now().toEpochMilli();
//            long elapsedTime = currentTime - lastLeakTime.get();
//
//            double leaksAllowed = (elapsedTime*leakRatePerSecond)/1000;
//            long leaksAllowedToFloor = (long) Math.floor(leaksAllowed);
//            while (leaksAllowedToFloor>0 && !queue.isEmpty()){
//                queue.poll();
//                lastLeakTime.set(currentTime);
//                leaksAllowedToFloor--;
//            }
//        }
//
//        public synchronized boolean tryConsume(long tokens){
//            leak();
//            if(queue.size() + tokens > capacity) return false;
//            for(int i = 0 ; i < tokens; i++){
//                queue.add(Instant.now().toEpochMilli());
//            }
//            return true;
//        }
//
//        public synchronized long getAvailableTokens(){
//            leak();
//            return capacity-queue.size();
//        }
//    }
//
//    private final Map<String , Bucket> buckets = new ConcurrentHashMap<>();
//    private final long capacity;
//    private final long leakRate;
//
//    public LeakyBucketLimiterService(long capacity, long leakRate){
//        this.capacity = capacity;
//        this.leakRate = leakRate;
//    }
//    @Override
//    public boolean tryConsume(String identifier, long tokens) {
//        Bucket bucket = buckets.computeIfAbsent(
//                identifier,
//                k->new Bucket(capacity,leakRate/60.0)
//        );
//        boolean allowed = bucket.tryConsume(1);
//        log.debug("Leaky Bucket - {}: {} (available: {})",
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
