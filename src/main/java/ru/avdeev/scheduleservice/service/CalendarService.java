package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.CalendarDto;
import ru.avdeev.scheduleservice.dto.WorkTimeDto;

import java.time.LocalDate;
import java.util.UUID;

public interface CalendarService {

    Mono<CalendarDto> getByOwner(UUID ownerId);

    Mono<CalendarDto> add(CalendarDto calendarDto);

    Mono<WorkTimeDto> getWorkTime(UUID ownerId, LocalDate startDate, LocalDate endDate);
}
