package ru.avdeev.scheduleservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.WorkTimeDto;
import ru.avdeev.scheduleservice.service.WorkTimeService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/v1/work-time")
@RequiredArgsConstructor
public class WorkTimeController {

    private final WorkTimeService service;
    @GetMapping("")
    public Mono<WorkTimeDto> getWorkTime(
            @RequestParam(name = "owner") UUID ownerId, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {

        return service.getWorkTime(ownerId, startDate, endDate);
    }
}
