package com.example.webflux.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Validated
@Configuration
@ConfigurationProperties("configuration")
public class WebFluxConfiguration {

    @Valid
    @NotNull
    MultiplyService multiplyService;

    @Data
    public static class MultiplyService {
        @NotNull
        String baseUrl;
        @NotNull
        @Min(100)
        @Max(10000)
        Integer cacheSize;
        @NotNull
        @Min(1)
        @Max(60)
        Integer cacheExpireAfterMinutes;
    }

}
