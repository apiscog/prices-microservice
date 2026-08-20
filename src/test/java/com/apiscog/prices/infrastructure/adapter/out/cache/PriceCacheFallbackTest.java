package com.apiscog.prices.infrastructure.adapter.out.cache;

import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.application.port.in.FindApplicablePriceUseCase;
import com.apiscog.prices.domain.model.Price;
import com.apiscog.prices.infrastructure.adapter.out.persistence.PricePersistenceAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "spring.cache.type=none")
class PriceCacheFallbackTest {

    private static final String CACHE_KEY = "1:35455:2020-06-14T16:00";

    @Autowired
    private FindApplicablePriceUseCase useCase;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoSpyBean
    private PricePersistenceAdapter persistenceAdapter;

    @Test
    void fallsBackToH2WhenCacheReadFails() {
        Cache cache = controlledCache();
        given(cache.get(CACHE_KEY)).willThrow(new IllegalStateException("simulated cache read failure"));
        FindApplicablePriceQuery query = query();

        Price result = useCase.findApplicablePrice(query);

        assertThat(result.priceList()).isEqualTo(2);
        verify(cache).get(CACHE_KEY);
        verify(persistenceAdapter).loadApplicablePrice(query);
    }

    @Test
    void returnsThePriceWhenCacheWriteFails() {
        Cache cache = controlledCache();
        FindApplicablePriceQuery query = query();
        doThrow(new IllegalStateException("simulated cache write failure"))
                .when(cache).put(org.mockito.ArgumentMatchers.eq(CACHE_KEY), any(Price.class));

        Price result = useCase.findApplicablePrice(query);

        assertThat(result.priceList()).isEqualTo(2);
        verify(cache).get(CACHE_KEY);
        verify(cache).put(CACHE_KEY, result);
        verify(persistenceAdapter).loadApplicablePrice(query);
    }

    private Cache controlledCache() {
        Cache cache = mock(Cache.class);
        given(cache.getName()).willReturn(PriceCacheAdapter.CACHE_NAME);
        given(cacheManager.getCache(PriceCacheAdapter.CACHE_NAME)).willReturn(cache);
        return cache;
    }

    private static FindApplicablePriceQuery query() {
        return new FindApplicablePriceQuery(LocalDateTime.of(2020, 6, 14, 16, 0), 35455, 1);
    }
}
