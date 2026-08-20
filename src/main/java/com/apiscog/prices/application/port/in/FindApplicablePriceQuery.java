package com.apiscog.prices.application.port.in;

import java.time.LocalDateTime;
import java.util.Objects;

public record FindApplicablePriceQuery(
        LocalDateTime applicationDate,
        long productId,
        long brandId
) {

    public FindApplicablePriceQuery {
        Objects.requireNonNull(applicationDate, "applicationDate must not be null");
        if (productId <= 0) {
            throw new IllegalArgumentException("productId must be positive");
        }
        if (brandId <= 0) {
            throw new IllegalArgumentException("brandId must be positive");
        }
    }
}
