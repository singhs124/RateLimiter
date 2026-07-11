package com.sushant.RateLimiter.infra.dsRouting;

public class ShardContextHolder {
    private static final ThreadLocal<Integer> CONTEXT = new ThreadLocal<>();

    public static void set(Integer shardId){
        CONTEXT.set(shardId);
    }

    public static Integer getShardID(){
        Integer shardId = CONTEXT.get();
        System.out.println("SHardId is: " + shardId);
        return shardId;
    }

    public static void clear(){
        CONTEXT.remove();
    }
}
