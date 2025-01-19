package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PriceService {

    Mono<Double> getAmount(UUID resourceId, Double count);
}
