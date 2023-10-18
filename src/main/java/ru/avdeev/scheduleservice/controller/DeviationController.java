package ru.avdeev.scheduleservice.controller;

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
        return  service.getByDateV2(calendarId, startDate, endDate);
    }

    @PostMapping("{calendarId}/deviation")
    public Mono<Void> add(@RequestBody DeviationDto deviation) {
        return service.add(deviation);
    }
}
