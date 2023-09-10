package ru.avdeev.scheduleservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.CalendarDto;
import ru.avdeev.scheduleservice.service.CalendarService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService service;
    @GetMapping("{ownerId}")
    public Mono<CalendarDto> getByOwnerId(@PathVariable UUID ownerId) {
        return service.getByOwner(ownerId);
    }

    @PostMapping("")
    public Mono<CalendarDto> add(@RequestBody CalendarDto calendarDto, @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getClaim("sub").toString();
        calendarDto.setUserId(UUID.fromString(userId));
        return service.add(calendarDto);
    }
}
