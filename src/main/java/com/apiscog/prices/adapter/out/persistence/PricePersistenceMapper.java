package com.apiscog.prices.adapter.out.persistence;

import com.apiscog.prices.domain.model.Price;
import org.springframework.stereotype.Component;

@Component
public final class PricePersistenceMapper {

    public Price toDomain(PriceJpaEntity entity) {
        return new Price(
                entity.getBrandId(),
                entity.getProductId(),
                entity.getPriceList(),
                entity.getPriority(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPrice(),
                entity.getCurrency()
        );
    }
}
