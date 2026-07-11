package com.sushant.RateLimiter.infra.dsRouting;


import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ShardDSRouting extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return ShardContextHolder.getShardID();
//        if(shardId == null) throw new IllegalStateException("Shard Context is not Set");
//        return shardId;
    }
}
