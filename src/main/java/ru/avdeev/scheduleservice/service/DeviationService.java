package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DeviationDto;

import java.time.LocalDate;
import java.util.UUID;

public interface DeviationService {

    Mono<DeviationDto> add(DeviationDto deviation, UUID calendarId);
    Mono<Void> delete(UUID id);
    Mono<Void> delete(UUID calendarId, LocalDate date);

    Flux<DeviationDto> getByDateInterval(UUID calendarId, LocalDate startDate, LocalDate endDate);
}
