package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DeviationDto;

import java.time.LocalDate;
import java.util.UUID;

public interface DeviationService {

    Mono<Void> add(DeviationDto deviation);

    Flux<DeviationDto> getByDateInterval(UUID calendarId, LocalDate startDate, LocalDate endDate);
}
