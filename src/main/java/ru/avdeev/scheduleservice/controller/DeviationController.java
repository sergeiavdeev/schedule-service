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
            @RequestParam(
                    name = "start",
                    required = false,
                    defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate startDate,
            @RequestParam(
                    name = "end",
                    required = false,
                    defaultValue = "#{T(java.time.LocalDate).now().plusYears(1)}") LocalDate endDate) {
        return  service.getByDateInterval(calendarId, startDate, endDate);
    }

    @PostMapping("{calendarId}/deviation")
    public Mono<Void> add(@RequestBody DeviationDto deviation, @PathVariable UUID calendarId) {
        return service.add(deviation, calendarId);
    }

    @DeleteMapping("/deviation/{id}")
    public Mono<Void> delete(@PathVariable UUID id) {
        return service.delete(id);
    }
}
