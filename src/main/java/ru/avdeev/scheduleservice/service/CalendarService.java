package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.CalendarDto;

import java.util.UUID;

public interface CalendarService {

    Mono<CalendarDto> getByOwner(UUID ownerId);

    Mono<CalendarDto> add(CalendarDto calendarDto);
}
