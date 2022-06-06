package com.example.webflux.service;

import com.example.webflux.client.MultiplyClient;
import com.example.webflux.model.CacheType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ValueService {

    private final MultiplyClient fluxClient;

    public ValueService(MultiplyClient fluxClient) {
        this.fluxClient = fluxClient;
    }

    public Flux<String> getValues(int numberOfValues, CacheType cacheType) {
        log.info("requesting {} random values", numberOfValues);
        return Flux.range(0, numberOfValues)
                .flatMap(i -> getMultipliedValue(i, 2, cacheType));
    }

    private Mono<String> getMultipliedValue(int first, int second, CacheType cacheType) {
        return switch (cacheType) {
            case NONE -> fluxClient.getMultipliedValue(first, second);
            case CAFFEINE -> fluxClient.getMultipliedValueWithCaffeineCache(first, second);
            case CACHEABLE -> fluxClient.getMultipliedValueWithCacheableCache(first, second);
        };
    }

}
