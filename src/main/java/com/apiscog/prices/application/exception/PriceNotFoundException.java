package com.apiscog.prices.application.exception;

import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;

import java.util.Objects;

public final class PriceNotFoundException extends RuntimeException {

    private final FindApplicablePriceQuery query;

    public PriceNotFoundException(FindApplicablePriceQuery query) {
        super(buildMessage(query));
        this.query = query;
    }

    public FindApplicablePriceQuery query() {
        return query;
    }

    private static String buildMessage(FindApplicablePriceQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        return "No applicable price found for productId %d, brandId %d at %s"
                .formatted(query.productId(), query.brandId(), query.applicationDate());
    }
}
