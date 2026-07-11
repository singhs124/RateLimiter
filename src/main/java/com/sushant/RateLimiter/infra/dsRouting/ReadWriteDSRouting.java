package com.sushant.RateLimiter.infra.dsRouting;

import com.sushant.RateLimiter.infra.dto.DSType;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReadWriteDSRouting extends AbstractRoutingDataSource {
    @Override
    protected @Nullable Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? DSType.READ
                : DSType.WRITE;
    }
}
