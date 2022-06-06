package com.example.webflux.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Pair<F, S> {

    F first;
    S second;

    public static <F, S> Pair<F, S> of(F first, S second) {
        return Pair.<F, S>builder()
                .first(first)
                .second(second)
                .build();
    }
}
