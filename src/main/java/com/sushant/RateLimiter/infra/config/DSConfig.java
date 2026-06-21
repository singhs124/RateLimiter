package com.sushant.RateLimiter.infra.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DSConfig {

    @Bean
    public DataSource primaryDS(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://postgres-primary:5432/ratelimiter");
//        config.setJdbcUrl("jdbc:postgresql://localhost:5432/expense");
        config.setUsername("ratelimiter");
        config.setPassword("root");
        config.setMaximumPoolSize(20);
        return new HikariDataSource(config);
    }

    @Bean
    public DataSource replicaDS(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://postgres-replica:5432/ratelimiter");
//        config.setJdbcUrl("jdbc:postgresql://localhost:5432/expense");
        config.setUsername("ratelimiter");
        config.setPassword("root");
        config.setMaximumPoolSize(50);
        return new HikariDataSource(config);
    }
}
