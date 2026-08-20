package com.apiscog.prices.application.service;

import com.apiscog.prices.application.exception.PriceNotFoundException;
import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.application.port.in.FindApplicablePriceUseCase;
import com.apiscog.prices.application.port.out.LoadApplicablePricePort;
import com.apiscog.prices.domain.model.Price;

import java.util.Objects;

public final class FindApplicablePriceService implements FindApplicablePriceUseCase {

    private final LoadApplicablePricePort loadApplicablePricePort;

    public FindApplicablePriceService(LoadApplicablePricePort loadApplicablePricePort) {
        this.loadApplicablePricePort = Objects.requireNonNull(
                loadApplicablePricePort,
                "loadApplicablePricePort must not be null"
        );
    }

    @Override
    public Price findApplicablePrice(FindApplicablePriceQuery query) {
        Objects.requireNonNull(query, "query must not be null");
        return loadApplicablePricePort.loadApplicablePrice(query)
                .orElseThrow(() -> new PriceNotFoundException(query));
    }
}
