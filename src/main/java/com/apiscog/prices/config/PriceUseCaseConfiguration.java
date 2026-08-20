package com.apiscog.prices.config;

import com.apiscog.prices.application.port.in.FindApplicablePriceUseCase;
import com.apiscog.prices.application.port.out.LoadApplicablePricePort;
import com.apiscog.prices.application.service.FindApplicablePriceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PriceUseCaseConfiguration {

    @Bean
    FindApplicablePriceUseCase findApplicablePriceUseCase(LoadApplicablePricePort loadApplicablePricePort) {
        return new FindApplicablePriceService(loadApplicablePricePort);
    }
}
