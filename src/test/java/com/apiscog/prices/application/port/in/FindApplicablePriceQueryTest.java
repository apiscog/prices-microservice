package com.apiscog.prices.application.port.in;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class FindApplicablePriceQueryTest {

    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 16, 0);

    @Test
    void acceptsValidCriteria() {
        FindApplicablePriceQuery query = new FindApplicablePriceQuery(APPLICATION_DATE, 35455, 1);

        assertThat(query.applicationDate()).isEqualTo(APPLICATION_DATE);
        assertThat(query.productId()).isEqualTo(35455);
        assertThat(query.brandId()).isEqualTo(1);
    }

    @Test
    void rejectsNullApplicationDate() {
        assertThatNullPointerException()
                .isThrownBy(() -> new FindApplicablePriceQuery(null, 35455, 1))
                .withMessage("applicationDate must not be null");
    }

    @Test
    void rejectsNonPositiveIdentifiers() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new FindApplicablePriceQuery(APPLICATION_DATE, 0, 1));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new FindApplicablePriceQuery(APPLICATION_DATE, 35455, 0));
    }
}
