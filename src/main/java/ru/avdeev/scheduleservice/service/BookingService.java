package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.BookingDto;
import ru.avdeev.scheduleservice.dto.DebtDto;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    Mono<BookingDto> save(BookingDto bookingDto);
    Flux<BookingDto> findAll();
    Mono<BookingDto> findById(UUID id);
    Flux<BookingDto> findByUser(UUID userId);
    Flux<BookingDto> findAllAfterCurrentDate();
    Mono<UUID> deleteById(UUID id);
    Mono<DebtDto> pay(UUID orderId, Double sum);

    Flux<BookingDto> findAllByResourcesAfterCurrentDate(List<UUID> resources);
}
