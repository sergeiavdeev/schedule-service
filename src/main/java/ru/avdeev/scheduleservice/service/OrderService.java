package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DebtDto;
import ru.avdeev.scheduleservice.dto.OrderDto;

import java.util.UUID;

public interface OrderService {
    Mono<OrderDto> save(OrderDto orderDto);
    Flux<OrderDto> findAll();
    Mono<OrderDto> findById(UUID id);
    Flux<OrderDto> findByUser(UUID userId);
    Flux<OrderDto> findAllAfterCurrentDate();
    Mono<UUID> deleteById(UUID id);
    Mono<DebtDto> pay(UUID orderId, Double sum);
}
