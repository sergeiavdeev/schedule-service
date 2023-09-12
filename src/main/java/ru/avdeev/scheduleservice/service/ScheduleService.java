package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Flux;
import ru.avdeev.scheduleservice.dto.ScheduleDto;

import java.util.List;
import java.util.UUID;

public interface ScheduleService {

    Flux<ScheduleDto> getByCalendarId(UUID calendarId);

    Flux<ScheduleDto> saveList(List<ScheduleDto> schedules, UUID calendarId);
}
