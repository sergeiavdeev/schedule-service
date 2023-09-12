package ru.avdeev.scheduleservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.avdeev.scheduleservice.dto.ScheduleDto;
import ru.avdeev.scheduleservice.service.ScheduleService;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@AllArgsConstructor
public class ScheduleController {

    private final ScheduleService service;

    @GetMapping("/calendar/{calendarId}/schedule")
    public Flux<ScheduleDto> getByCalendarId(@PathVariable UUID calendarId) {
        return service.getByCalendarId(calendarId);
    }
}
