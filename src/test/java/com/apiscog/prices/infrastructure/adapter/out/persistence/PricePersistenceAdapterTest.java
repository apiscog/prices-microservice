package com.apiscog.prices.infrastructure.adapter.out.persistence;

import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.application.port.out.LoadApplicablePricePort;
import com.apiscog.prices.domain.model.Price;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({PricePersistenceAdapter.class, PricePersistenceMapper.class})
class PricePersistenceAdapterTest {

    private static final long PRODUCT_ID = 35455;
    private static final long BRAND_ID = 1;

    @Autowired
    private LoadApplicablePricePort adapter;

    @ParameterizedTest
    @MethodSource("applicablePriceScenarios")
    void findsTheApplicablePriceWithTheHighestPriority(
            LocalDateTime applicationDate,
            long expectedPriceList,
            String expectedAmount
    ) {
        FindApplicablePriceQuery query =
                new FindApplicablePriceQuery(applicationDate, PRODUCT_ID, BRAND_ID);

        assertThat(adapter.loadApplicablePrice(query))
                .isPresent()
                .get()
                .satisfies(price -> {
                    assertThat(price.priceList()).isEqualTo(expectedPriceList);
                    assertThat(price.price()).isEqualByComparingTo(expectedAmount);
                    assertThat(price.currency()).isEqualTo("EUR");
                });
    }

    @ParameterizedTest
    @MethodSource("inclusiveBoundaryScenarios")
    void includesBothStartAndEndDates(LocalDateTime applicationDate, long expectedPriceList) {
        FindApplicablePriceQuery query =
                new FindApplicablePriceQuery(applicationDate, PRODUCT_ID, BRAND_ID);

        assertThat(adapter.loadApplicablePrice(query))
                .isPresent()
                .get()
                .extracting(Price::priceList)
                .isEqualTo(expectedPriceList);
    }

    @ParameterizedTest
    @MethodSource("missingPriceScenarios")
    void returnsEmptyWhenNoPriceMatches(FindApplicablePriceQuery query) {
        assertThat(adapter.loadApplicablePrice(query)).isEmpty();
    }

    @Test
    void mapsAllPersistenceFieldsToTheDomain() {
        FindApplicablePriceQuery query = new FindApplicablePriceQuery(
                LocalDateTime.of(2020, 6, 14, 16, 0),
                PRODUCT_ID,
                BRAND_ID
        );

        Price price = adapter.loadApplicablePrice(query).orElseThrow();

        assertThat(price).isEqualTo(new Price(
                BRAND_ID,
                PRODUCT_ID,
                2,
                1,
                LocalDateTime.of(2020, 6, 14, 15, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30),
                new BigDecimal("25.45"),
                "EUR"
        ));
    }

    private static Stream<Arguments> applicablePriceScenarios() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2020, 6, 14, 10, 0), 1, "35.50"),
                Arguments.of(LocalDateTime.of(2020, 6, 14, 16, 0), 2, "25.45"),
                Arguments.of(LocalDateTime.of(2020, 6, 14, 21, 0), 1, "35.50"),
                Arguments.of(LocalDateTime.of(2020, 6, 15, 10, 0), 3, "30.50"),
                Arguments.of(LocalDateTime.of(2020, 6, 16, 21, 0), 4, "38.95")
        );
    }

    private static Stream<Arguments> inclusiveBoundaryScenarios() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2020, 6, 14, 0, 0), 1),
                Arguments.of(LocalDateTime.of(2020, 6, 14, 15, 0), 2),
                Arguments.of(LocalDateTime.of(2020, 6, 14, 18, 30), 2),
                Arguments.of(LocalDateTime.of(2020, 6, 15, 0, 0), 3),
                Arguments.of(LocalDateTime.of(2020, 6, 15, 11, 0), 3),
                Arguments.of(LocalDateTime.of(2020, 6, 15, 16, 0), 4),
                Arguments.of(LocalDateTime.of(2020, 12, 31, 23, 59, 59), 4)
        );
    }

    private static Stream<FindApplicablePriceQuery> missingPriceScenarios() {
        return Stream.of(
                new FindApplicablePriceQuery(LocalDateTime.of(2020, 6, 13, 23, 59, 59), PRODUCT_ID, BRAND_ID),
                new FindApplicablePriceQuery(LocalDateTime.of(2021, 1, 1, 0, 0), PRODUCT_ID, BRAND_ID),
                new FindApplicablePriceQuery(LocalDateTime.of(2020, 6, 14, 16, 0), 99999, BRAND_ID),
                new FindApplicablePriceQuery(LocalDateTime.of(2020, 6, 14, 16, 0), PRODUCT_ID, 2)
        );
    }
}
