package com.apiscog.prices.adapter.out.persistence;

import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.application.port.out.LoadApplicablePricePort;
import com.apiscog.prices.domain.model.Price;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class PricePersistenceAdapter implements LoadApplicablePricePort {

    private final SpringDataPriceRepository repository;
    private final PricePersistenceMapper mapper;

    public PricePersistenceAdapter(
            SpringDataPriceRepository repository,
            PricePersistenceMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Price> loadApplicablePrice(FindApplicablePriceQuery query) {
        return repository
                .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        query.brandId(),
                        query.productId(),
                        query.applicationDate(),
                        query.applicationDate()
                )
                .map(mapper::toDomain);
    }
}
