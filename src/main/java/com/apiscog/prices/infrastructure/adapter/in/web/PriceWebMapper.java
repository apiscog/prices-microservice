package com.apiscog.prices.infrastructure.adapter.in.web;

import com.apiscog.prices.domain.model.Price;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class PriceWebMapper {

    public PriceResponse toResponse(Price price) {
        Objects.requireNonNull(price, "price must not be null");
        return new PriceResponse(
                price.productId(),
                price.brandId(),
                price.priceList(),
                price.startDate(),
                price.endDate(),
                price.price(),
                price.currency()
        );
    }
}
