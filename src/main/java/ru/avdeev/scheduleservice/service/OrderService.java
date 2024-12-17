package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.OrderDto;

import java.time.LocalDate;
import java.util.UUID;

public interface OrderService {
    Mono<OrderDto> save(OrderDto orderDto);
    Flux<OrderDto> findAll();
    Flux<OrderDto> findByUser(UUID userId);
    Flux<OrderDto> findAllAfterCurrentDate();

}
