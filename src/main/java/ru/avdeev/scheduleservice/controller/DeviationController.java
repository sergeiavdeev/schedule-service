package ru.avdeev.scheduleservice.controller;

import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DeviationDto;
import ru.avdeev.scheduleservice.service.DeviationService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/v1/calendar")
@RequiredArgsConstructor
public class DeviationController {

    private final DeviationService service;

    @GetMapping("{calendarId}/deviation")
    public Flux<DeviationDto> getByDate(
            @PathVariable UUID calendarId,
            @RequestParam("start") LocalDate startDate,
            @RequestParam("end") LocalDate endDate) {
        return  service.getByDate(calendarId, startDate, endDate);
    }
}
