package com.example.webflux.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient multiplyWebClient(WebFluxConfiguration webFluxConfiguration) {
        return WebClient.builder()
                .baseUrl(webFluxConfiguration.getMultiplyService().getBaseUrl())
                .build();
    }
}
