package com.example.webflux.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MultiplyService {

    private final AtomicInteger rateLimit = new AtomicInteger(0);

    public Mono<String> getMultipliedValue(int first, int second) {
        if (rateLimit.getAndIncrement() < 1) {
            rateLimit.decrementAndGet();
            return Mono.just(String.valueOf(first * second));
        }
        rateLimit.decrementAndGet();
        throw new RuntimeException("Requests too fast, slow down!");

    }
}
