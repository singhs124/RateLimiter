package com.sushant.RateLimiter.infra.config;

import com.sushant.RateLimiter.infra.dsRouting.ReadWriteDSRouting;
import com.sushant.RateLimiter.infra.dsRouting.ShardDSRouting;
import com.sushant.RateLimiter.infra.dto.DSType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RoutingDSConfig {
    private final DSConfig dsConfig;

    public DataSource buildReadWriteDS(String primaryUrl, String replicaUrl){
        DataSource primary = dsConfig.createDS(primaryUrl,20);
        DataSource replica = dsConfig.createDS(replicaUrl,50);

        ReadWriteDSRouting routingDS = new ReadWriteDSRouting();
        Map<Object,Object> dsMap = new HashMap<>();
        dsMap.put(DSType.WRITE, primary);
        dsMap.put(DSType.READ, replica);
        routingDS.setTargetDataSources(dsMap);
        routingDS.setDefaultTargetDataSource(primary);
        routingDS.afterPropertiesSet();
        return routingDS;
    }


    @Bean
    public DataSource shardRoutingDS(){
        Map<Object,Object> shardMap = new HashMap<>();

        shardMap.put(0, buildReadWriteDS(
                "jdbc:postgresql://postgres-0-primary:5432/ratelimiter",
                "jdbc:postgresql://postgres-0-replica:5432/ratelimiter"
        ));

        shardMap.put(1, buildReadWriteDS(
                "jdbc:postgresql://postgres-1-primary:5432/ratelimiter",
                "jdbc:postgresql://postgres-1-replica:5432/ratelimiter"
        ));

        shardMap.put(2, buildReadWriteDS(
                "jdbc:postgresql://postgres-2-primary:5432/ratelimiter",
                "jdbc:postgresql://postgres-2-replica:5432/ratelimiter"
        ));

        shardMap.put(3, buildReadWriteDS(
                "jdbc:postgresql://postgres-3-primary:5432/ratelimiter",
                "jdbc:postgresql://postgres-3-replica:5432/ratelimiter"
        ));
        ShardDSRouting shardDSRouting = new ShardDSRouting();
        shardDSRouting.setTargetDataSources(shardMap);
        shardDSRouting.setDefaultTargetDataSource(shardMap.get(0));
        shardDSRouting.afterPropertiesSet();

        return shardDSRouting;
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("shardRoutingDS") DataSource shardRoutingDS) {
        return new LazyConnectionDataSourceProxy(shardRoutingDS);
    }
}
