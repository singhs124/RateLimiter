package com.sushant.RateLimiter.infra.dsRouting;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ReadWriteDSRouting extends AbstractRoutingDataSource {
    @Override
    protected @Nullable Object determineCurrentLookupKey() {
        return DSContextHolder.getDSType();
    }
}
