package com.example.webflux.controller;

import com.example.webflux.service.MultiplyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("multiply")
public class MultiplyController {

    private final MultiplyService multiplyService;

    public MultiplyController(MultiplyService multiplyService) {
        this.multiplyService = multiplyService;
    }

    @GetMapping("first/{first}/second/{second}")
    public Mono<String> getMultipliedValue(@PathVariable int first, @PathVariable int second) {
        return multiplyService.getMultipliedValue(first, second);
    }
}
