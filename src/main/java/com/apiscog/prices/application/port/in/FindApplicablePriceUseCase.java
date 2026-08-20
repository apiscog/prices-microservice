package com.apiscog.prices.application.port.in;

import com.apiscog.prices.domain.model.Price;

public interface FindApplicablePriceUseCase {

    Price findApplicablePrice(FindApplicablePriceQuery query);
}
