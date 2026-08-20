package com.apiscog.prices.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class PriceTest {

    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

    @Test
    void acceptsValidValuesIncludingNonNegativeBoundaries() {
        Price price = new Price(1, 35455, 1, 0, START_DATE, START_DATE, BigDecimal.ZERO, "EUR");

        assertThat(price.priority()).isZero();
        assertThat(price.price()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(price.startDate()).isEqualTo(price.endDate());
    }

    @Test
    void rejectsInvalidIdentifiersAndPriority() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> priceWith(0, 35455, 1, 0, BigDecimal.ONE, "EUR"));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> priceWith(1, 0, 1, 0, BigDecimal.ONE, "EUR"));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> priceWith(1, 35455, 0, 0, BigDecimal.ONE, "EUR"));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> priceWith(1, 35455, 1, -1, BigDecimal.ONE, "EUR"));
    }

    @Test
    void rejectsInvalidDateRange() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Price(
                        1, 35455, 1, 0, END_DATE, START_DATE, BigDecimal.ONE, "EUR"
                ));
        assertThatNullPointerException()
                .isThrownBy(() -> new Price(
                        1, 35455, 1, 0, null, END_DATE, BigDecimal.ONE, "EUR"
                ));
    }

    @Test
    void rejectsInvalidPrice() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> priceWith(1, 35455, 1, 0, new BigDecimal("-0.01"), "EUR"));
        assertThatNullPointerException()
                .isThrownBy(() -> priceWith(1, 35455, 1, 0, null, "EUR"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"EUR", "eur", "eUr", " eur "})
    void normalizesValidCurrencyCodes(String currency) {
        Price price = priceWith(1, 35455, 1, 0, BigDecimal.ONE, currency);

        assertThat(price.currency()).isEqualTo("EUR");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "123", "ZZZ", "EU"})
    void rejectsInvalidCurrencyCodes(String currency) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> priceWith(1, 35455, 1, 0, BigDecimal.ONE, currency))
                .withMessage("currency must be a valid ISO 4217 code");
    }

    @Test
    void rejectsNullCurrency() {
        assertThatNullPointerException()
                .isThrownBy(() -> priceWith(1, 35455, 1, 0, BigDecimal.ONE, null))
                .withMessage("currency must not be null");
    }

    private Price priceWith(
            long brandId,
            long productId,
            long priceList,
            int priority,
            BigDecimal amount,
            String currency
    ) {
        return new Price(
                brandId,
                productId,
                priceList,
                priority,
                START_DATE,
                END_DATE,
                amount,
                currency
        );
    }
}
