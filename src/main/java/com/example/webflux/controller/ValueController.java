package com.example.webflux.controller;

import com.example.webflux.controller.api.ValueDTO;
import com.example.webflux.model.CacheType;
import com.example.webflux.service.ValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("values")
public class ValueController {

    private final ValueService valueService;

    public ValueController(ValueService valueService) {
        this.valueService = valueService;
    }

    @GetMapping("{numberOfValues}")
    public Flux<ValueDTO> getValues(@PathVariable int numberOfValues) {
        return valueService.getValues(numberOfValues, CacheType.NONE)
                .map(s -> ValueDTO.builder().value(s).build());
    }

    @GetMapping("caffeine/{numberOfValues}")
    public Flux<ValueDTO> getValuesWithCaffeine(@PathVariable int numberOfValues) {
        return valueService.getValues(numberOfValues, CacheType.CAFFEINE)
                .map(s -> ValueDTO.builder().value(s).build());
    }

    @GetMapping("cacheable/{numberOfValues}")
    public Flux<ValueDTO> getValuesWithCacheable(@PathVariable int numberOfValues) {
        return valueService.getValues(numberOfValues, CacheType.CACHEABLE)
                .map(s -> ValueDTO.builder().value(s).build());
    }
}