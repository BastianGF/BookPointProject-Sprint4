package com.bookpoint.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/logistic")
    public Mono<Map<String, String>> logisticFallback() {
        Map<String, String> fallback = new HashMap<>();
        fallback.put("error", "Logistic Service no está disponible");
        fallback.put("status", "503");
        fallback.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return Mono.just(fallback);
    }

    @GetMapping("/supplier")
    public Mono<Map<String, String>> supplierFallback() {
        Map<String, String> fallback = new HashMap<>();
        fallback.put("error", "Supplier Service no está disponible");
        fallback.put("status", "503");
        fallback.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return Mono.just(fallback);
    }
}