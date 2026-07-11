package com.sushant.RateLimiter.infra.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DSConfig {

    public DataSource createDS(String url, int maxPoolSize){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername("ratelimiter");
        config.setPassword("root");
        config.setMaximumPoolSize(maxPoolSize);
        return new HikariDataSource(config);
    }
}
