package com.apiscog.prices.application.port.out;

import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.domain.model.Price;

import java.util.Optional;

public interface LoadApplicablePricePort {

    Optional<Price> loadApplicablePrice(FindApplicablePriceQuery query);
}
