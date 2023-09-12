package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DeviationDto;

import java.time.LocalDate;

public interface DeviationService {

    Flux<DeviationDto> getByDate(LocalDate startDate, LocalDate endDate);
    Mono<DeviationDto> add(DeviationDto deviation);
}
