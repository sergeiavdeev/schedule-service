package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.CalendarDto;
import ru.avdeev.scheduleservice.mapper.CalendarMapper;
import ru.avdeev.scheduleservice.repository.CalendarRepository;
import ru.avdeev.scheduleservice.service.CalendarService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository repository;
    private final CalendarMapper mapper;
    @Override
    public Mono<CalendarDto> getByOwner(UUID ownerId) {
        return repository.findTop1ByOwnerIdOrderByStartDateDesc(ownerId)
                .map(mapper::toDto);
    }

    @Override
    public Mono<CalendarDto> add(CalendarDto calendarDto) {
        return repository.save(mapper.toEntity(calendarDto))
                .map(mapper::toDto);
    }
}
