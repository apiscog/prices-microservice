package com.apiscog.prices.application.service;

import com.apiscog.prices.application.exception.PriceNotFoundException;
import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.application.port.out.LoadApplicablePricePort;
import com.apiscog.prices.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindApplicablePriceServiceTest {

    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2020, 6, 14, 16, 0);
    private static final FindApplicablePriceQuery QUERY =
            new FindApplicablePriceQuery(APPLICATION_DATE, 35455, 1);
    private static final Price PRICE = new Price(
            1,
            35455,
            2,
            1,
            LocalDateTime.of(2020, 6, 14, 15, 0),
            LocalDateTime.of(2020, 6, 14, 18, 30),
            new BigDecimal("25.45"),
            "EUR"
    );

    @Mock
    private LoadApplicablePricePort loadApplicablePricePort;

    private FindApplicablePriceService service;

    @BeforeEach
    void setUp() {
        service = new FindApplicablePriceService(loadApplicablePricePort);
    }

    @Test
    void returnsExactlyThePriceLoadedForTheSuppliedCriteria() {
        when(loadApplicablePricePort.loadApplicablePrice(QUERY)).thenReturn(Optional.of(PRICE));

        Price result = service.findApplicablePrice(QUERY);

        assertThat(result).isSameAs(PRICE);
        verify(loadApplicablePricePort).loadApplicablePrice(QUERY);
        verifyNoMoreInteractions(loadApplicablePricePort);
    }

    @Test
    void throwsPriceNotFoundExceptionWhenNoPriceExists() {
        when(loadApplicablePricePort.loadApplicablePrice(QUERY)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findApplicablePrice(QUERY))
                .isInstanceOfSatisfying(PriceNotFoundException.class, exception -> {
                    assertThat(exception.query()).isEqualTo(QUERY);
                    assertThat(exception).hasMessageContaining("productId 35455");
                });
        verify(loadApplicablePricePort).loadApplicablePrice(QUERY);
        verifyNoMoreInteractions(loadApplicablePricePort);
    }

    @Test
    void rejectsNullQueryWithoutCallingThePort() {
        assertThatNullPointerException()
                .isThrownBy(() -> service.findApplicablePrice(null))
                .withMessage("query must not be null");
        verifyNoInteractions(loadApplicablePricePort);
    }
}
