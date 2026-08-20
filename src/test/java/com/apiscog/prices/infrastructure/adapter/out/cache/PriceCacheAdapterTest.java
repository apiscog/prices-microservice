package com.apiscog.prices.infrastructure.adapter.out.cache;

import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.application.port.out.LoadApplicablePricePort;
import com.apiscog.prices.domain.model.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

class PriceCacheAdapterTest {

    private final LoadApplicablePricePort delegate = mock(LoadApplicablePricePort.class);
    private final PriceCacheAdapter adapter = new PriceCacheAdapter(delegate);

    @Test
    void delegatesTheQueryAndReturnsTheLoadedPrice() {
        FindApplicablePriceQuery query = query();
        Optional<Price> expected = Optional.of(price());
        given(delegate.loadApplicablePrice(query)).willReturn(expected);

        Optional<Price> result = adapter.loadApplicablePrice(query);

        assertThat(result).isSameAs(expected);
        verify(delegate, only()).loadApplicablePrice(query);
    }

    @Test
    void preservesAnEmptyResult() {
        FindApplicablePriceQuery query = query();
        given(delegate.loadApplicablePrice(query)).willReturn(Optional.empty());

        assertThat(adapter.loadApplicablePrice(query)).isEmpty();
        verify(delegate, only()).loadApplicablePrice(query);
    }

    @Test
    void rejectsANullDelegate() {
        assertThatNullPointerException()
                .isThrownBy(() -> new PriceCacheAdapter(null))
                .withMessage("delegate must not be null");
    }

    private static FindApplicablePriceQuery query() {
        return new FindApplicablePriceQuery(LocalDateTime.of(2020, 6, 14, 16, 0), 35455, 1);
    }

    private static Price price() {
        return new Price(
                1,
                35455,
                2,
                1,
                LocalDateTime.of(2020, 6, 14, 15, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30),
                new BigDecimal("25.45"),
                "EUR"
        );
    }
}
