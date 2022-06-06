package com.example.webflux.client;

import com.example.webflux.configuration.WebFluxConfiguration;
import com.example.webflux.model.CacheType;
import com.example.webflux.model.Pair;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Component
public class MultiplyClient {

    private final WebClient MultiplyWebClient;
    private final Cache<Pair<Integer, Integer>, String> multiplyCaffeineCache;
    private final Duration monoCacheDuration;

    public MultiplyClient(WebClient MultiplyWebClient, WebFluxConfiguration webFluxConfiguration) {
        this.MultiplyWebClient = MultiplyWebClient;
        WebFluxConfiguration.MultiplyService multiplyService = webFluxConfiguration.getMultiplyService();
        multiplyCaffeineCache = setupCaffeineCache(multiplyService.getCacheSize(), multiplyService.getCacheExpireAfterMinutes());
        monoCacheDuration = Duration.ofMinutes(multiplyService.getCacheExpireAfterMinutes() + 1);
    }

    public Mono<String> getMultipliedValue(int first, int second) {
        return MultiplyWebClient.get()
                .uri(createUri(first, second))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(300))
                .doOnError(throwable -> log.error("Failed to get value. {}", throwable.getMessage(), throwable))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(10)));
    }

    public Mono<String> getMultipliedValueWithCaffeineCache(int first, int second) {
        Pair<Integer, Integer> key = Pair.of(first, second);
        return Optional.ofNullable(multiplyCaffeineCache.getIfPresent(key))
                .map(Mono::just)
                .orElse(getMultipliedValueAndCache(first, second, key));
    }

    @Cacheable("multiplied")
    public Mono<String> getMultipliedValueWithCacheableCache(int first, int second) {
        return getMultipliedValue(first, second).cache(monoCacheDuration);
    }

    private Mono<String> getMultipliedValueAndCache(int first, int second, Pair<Integer, Integer> key) {
        return getMultipliedValue(first, second)
                .doOnNext(s -> multiplyCaffeineCache.put(key, s));
    }

    private Function<UriBuilder, URI> createUri(int first, int second) {
        return uriBuilder -> uriBuilder
                .path("first/{first}/second/{second}")
                .build(first, second);
    }

    private Cache<Pair<Integer, Integer>, String> setupCaffeineCache(int maximumSize, int expireAfterMinutes) {
        log.info("Configuring cache. MaximumSize: {} ExpireAfterMinutes: {}", maximumSize, expireAfterMinutes);
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(expireAfterMinutes))
                .maximumSize(maximumSize)
                .build();
    }
}
