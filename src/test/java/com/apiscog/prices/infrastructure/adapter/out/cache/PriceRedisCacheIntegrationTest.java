package com.apiscog.prices.infrastructure.adapter.out.cache;

import com.apiscog.prices.application.exception.PriceNotFoundException;
import com.apiscog.prices.application.port.in.FindApplicablePriceQuery;
import com.apiscog.prices.application.port.in.FindApplicablePriceUseCase;
import com.apiscog.prices.domain.model.Price;
import com.apiscog.prices.infrastructure.adapter.out.persistence.PricePersistenceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = {
        "spring.cache.type=redis",
        "spring.cache.redis.time-to-live=30s"
})
@Testcontainers(disabledWithoutDocker = true)
class PriceRedisCacheIntegrationTest {

    private static final int REDIS_PORT = 6379;
    private static final int REDIS_DATABASE = 0;
    private static final String CACHE_KEY = "applicablePrices::1:35455:2020-06-14T16:00";

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.4.10"))
            .withExposedPorts(REDIS_PORT);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(REDIS_PORT));
    }

    @Autowired
    private FindApplicablePriceUseCase useCase;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private ApplicationContext applicationContext;

    @MockitoSpyBean
    private PricePersistenceAdapter persistenceAdapter;

    @BeforeEach
    void clearCacheAndInteractions() {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
        clearInvocations(persistenceAdapter);
    }

    @Test
    void servesRepeatedQueriesFromRedisWithJsonAndConfiguredTtl() {
        assertRedisInfrastructureUsesTheContainer();
        FindApplicablePriceQuery query = query(LocalDateTime.of(2020, 6, 14, 16, 0));

        Price firstResult = useCase.findApplicablePrice(query);
        Set<String> keys = redisTemplate.keys("*");
        assertThat(keys).hasSize(1);
        String key = keys.iterator().next();
        assertThat(key).isEqualTo(CACHE_KEY);
        assertThat(redisTemplate.opsForValue().get(key))
                .contains("com.apiscog.prices.domain.model.Price")
                .contains("brandId", "1")
                .contains("productId", "35455")
                .contains("priceList", "2")
                .contains("priority", "1")
                .contains("startDate", "2020-06-14T15:00")
                .contains("endDate", "2020-06-14T18:30")
                .contains("price", "25.45")
                .contains("currency", "EUR")
                .doesNotContain("\"id\":");
        assertThat(redisTemplate.getExpire(key, TimeUnit.SECONDS)).isBetween(1L, 30L);

        Price cachedResult = useCase.findApplicablePrice(query);

        assertThat(cachedResult).isEqualTo(firstResult);
        assertThat(cachedResult)
                .extracting(
                        Price::brandId,
                        Price::productId,
                        Price::priceList,
                        Price::priority,
                        Price::startDate,
                        Price::endDate,
                        Price::price,
                        Price::currency
                )
                .containsExactly(
                        1L,
                        35455L,
                        2L,
                        1,
                        LocalDateTime.of(2020, 6, 14, 15, 0),
                        LocalDateTime.of(2020, 6, 14, 18, 30),
                        firstResult.price(),
                        "EUR"
                );
        verify(persistenceAdapter, times(1)).loadApplicablePrice(query);
    }

    @Test
    void doesNotCacheMissingPrices() {
        FindApplicablePriceQuery query = query(LocalDateTime.of(2021, 1, 1, 0, 0));

        assertThatThrownBy(() -> useCase.findApplicablePrice(query))
                .isInstanceOf(PriceNotFoundException.class);
        assertThatThrownBy(() -> useCase.findApplicablePrice(query))
                .isInstanceOf(PriceNotFoundException.class);

        verify(persistenceAdapter, times(2)).loadApplicablePrice(query);
        assertThat(redisTemplate.keys("*")).isEmpty();
    }

    private void assertRedisInfrastructureUsesTheContainer() {
        assertThat(cacheManager).isExactlyInstanceOf(RedisCacheManager.class);
        assertThat(applicationContext.getBeansOfType(RedisConnectionFactory.class)).hasSize(1);
        assertThat(redisTemplate.getConnectionFactory()).isSameAs(connectionFactory);
        assertThat(connectionFactory).isInstanceOf(LettuceConnectionFactory.class);

        LettuceConnectionFactory lettuce = (LettuceConnectionFactory) connectionFactory;
        assertThat(lettuce.getHostName()).isEqualTo(REDIS.getHost());
        assertThat(lettuce.getPort()).isEqualTo(REDIS.getMappedPort(REDIS_PORT));
        assertThat(lettuce.getDatabase()).isEqualTo(REDIS_DATABASE);
    }

    private static FindApplicablePriceQuery query(LocalDateTime applicationDate) {
        return new FindApplicablePriceQuery(applicationDate, 35455, 1);
    }
}
