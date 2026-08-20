package com.apiscog.prices.infrastructure.config;

import com.apiscog.prices.application.port.out.LoadApplicablePricePort;
import com.apiscog.prices.domain.model.Price;
import com.apiscog.prices.infrastructure.adapter.out.cache.PriceCacheAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.cache.autoconfigure.CacheProperties;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.math.BigDecimal;
import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@EnableCaching
public class PriceCacheConfiguration implements CachingConfigurer {

    @Bean
    @Primary
    LoadApplicablePricePort cachedPricePort(
            @Qualifier("pricePersistenceAdapter") LoadApplicablePricePort persistencePort
    ) {
        return new PriceCacheAdapter(persistencePort);
    }

    @Bean
    RedisCacheManagerBuilderCustomizer priceCacheManagerBuilderCustomizer(
            CacheProperties properties,
            RedisConnectionFactory connectionFactory
    ) {
        Duration ttl = properties.getRedis().getTimeToLive();
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalStateException("spring.cache.redis.time-to-live must be positive");
        }

        var typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Price.class)
                .allowIfSubType(BigDecimal.class)
                .build();
        var serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(typeValidator)
                .customize(builder -> builder.findAndAddModules())
                .build();
        var serializationPair = RedisSerializationContext.SerializationPair.fromSerializer(serializer);
        var cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .serializeValuesWith(serializationPair);
        var cacheWriter = RedisCacheWriter.create(
                connectionFactory,
                RedisCacheWriter.RedisCacheWriterConfigurer::immediateWrites
        );

        return builder -> builder
                .cacheWriter(cacheWriter)
                .withCacheConfiguration(PriceCacheAdapter.CACHE_NAME, cacheConfiguration);
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RedisCacheErrorHandler();
    }
}
