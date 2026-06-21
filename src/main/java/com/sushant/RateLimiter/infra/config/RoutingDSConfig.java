package com.sushant.RateLimiter.infra.config;

import com.sushant.RateLimiter.infra.dsRouting.ReadWriteDSRouting;
import com.sushant.RateLimiter.infra.dto.DSType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RoutingDSConfig {

    @Bean
    @Primary
    public DataSource routingDS(
            @Qualifier("primaryDS") DataSource primary,
            @Qualifier("replicaDS") DataSource replica
    ){
        ReadWriteDSRouting routingDS = new ReadWriteDSRouting();
        Map<Object,Object> DSMap = new HashMap<>();
        DSMap.put(DSType.WRITE, primary);
        DSMap.put(DSType.READ, replica);
        routingDS.setTargetDataSources(DSMap);
        routingDS.setDefaultTargetDataSource(primary);
        routingDS.afterPropertiesSet();
        return routingDS;
    }
}
