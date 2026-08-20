package com.apiscog.prices.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

public record Price(
        long brandId,
        long productId,
        long priceList,
        int priority,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency
) {

    public Price {
        if (brandId <= 0) {
            throw new IllegalArgumentException("brandId must be positive");
        }
        if (productId <= 0) {
            throw new IllegalArgumentException("productId must be positive");
        }
        if (priceList <= 0) {
            throw new IllegalArgumentException("priceList must be positive");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("priority must be non-negative");
        }

        Objects.requireNonNull(startDate, "startDate must not be null");
        Objects.requireNonNull(endDate, "endDate must not be null");
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must not be after endDate");
        }

        Objects.requireNonNull(price, "price must not be null");
        if (price.signum() < 0) {
            throw new IllegalArgumentException("price must be non-negative");
        }

        Objects.requireNonNull(currency, "currency must not be null");
        String normalizedCurrency = currency.trim().toUpperCase(Locale.ROOT);
        try {
            currency = Currency.getInstance(normalizedCurrency).getCurrencyCode();
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("currency must be a valid ISO 4217 code");
        }
    }
}
