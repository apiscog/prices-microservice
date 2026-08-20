package com.apiscog.prices.infrastructure.adapter.out.cache;

import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.application.port.out.LoadApplicablePricePort;
import com.apiscog.prices.domain.model.Price;
import org.springframework.cache.annotation.Cacheable;

import java.util.Objects;
import java.util.Optional;

public class PriceCacheAdapter implements LoadApplicablePricePort {

    public static final String CACHE_NAME = "applicablePrices";

    private final LoadApplicablePricePort delegate;

    public PriceCacheAdapter(LoadApplicablePricePort delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
    }

    @Override
    @Cacheable(
            cacheNames = CACHE_NAME,
            key = "#query.brandId() + ':' + #query.productId() + ':' + #query.applicationDate()",
            unless = "#result == null"
    )
    public Optional<Price> loadApplicablePrice(FindApplicablePriceQuery query) {
        return delegate.loadApplicablePrice(query);
    }
}
