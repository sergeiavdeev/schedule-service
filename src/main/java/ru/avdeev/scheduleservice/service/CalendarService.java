package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.CalendarDto;
import java.time.LocalDate;
import java.util.UUID;

public interface CalendarService {

    Mono<CalendarDto> getByOwner(UUID ownerId);

    Mono<CalendarDto> add(CalendarDto calendarDto);

    Flux<CalendarDto> getAllByOwner(UUID ownerId, LocalDate startDate);
}
