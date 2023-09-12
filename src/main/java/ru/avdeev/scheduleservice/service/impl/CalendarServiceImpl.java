package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.CalendarDto;
import ru.avdeev.scheduleservice.dto.ScheduleDto;
import ru.avdeev.scheduleservice.dto.TimeIntervalDto;
import ru.avdeev.scheduleservice.mapper.CalendarMapper;
import ru.avdeev.scheduleservice.repository.CalendarRepository;
import ru.avdeev.scheduleservice.service.CalendarService;
import ru.avdeev.scheduleservice.service.ScheduleService;

import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository repository;
    private final CalendarMapper mapper;

    private final ScheduleService scheduleService;
    @Override
    public Mono<CalendarDto> getByOwner(UUID ownerId) {
        return repository.findTop1ByOwnerIdOrderByStartDateDesc(ownerId)
                .map(mapper::toDto)
                .flatMap(this::setSchedules);
    }

    @Override
    @Transactional
    public Mono<CalendarDto> add(CalendarDto calendarDto) {
        return repository.save(mapper.toEntity(calendarDto))
                .map(mapper::toDto)
                .flatMap(savedCalendar -> scheduleService.saveList(calendarDto.getSchedules(), savedCalendar.getId())
                        .collectList()
                        .map(savedSchedules -> {
                            savedCalendar.setSchedules(savedSchedules);
                            return groupIntervalByDayOfWeek(savedCalendar);
                        }));
    }

    private Mono<CalendarDto> setSchedules(CalendarDto calendarDto) {

        return scheduleService.getByCalendarId(calendarDto.getId())
                .collectList()
                .map(schedules -> {
                    calendarDto.setSchedules(schedules);
                    return calendarDto;
                })
                .map(this::groupIntervalByDayOfWeek);
    }

    private CalendarDto groupIntervalByDayOfWeek(CalendarDto calendar) {

        List<ScheduleDto> scheduleList = new ArrayList<>();
        calendar.getSchedules().stream().mapToInt(ScheduleDto::getDayOfWeek)
                .distinct()
                .forEach(i -> {
                    List<TimeIntervalDto> intervals = calendar.getSchedules().stream()
                            .filter(schedule -> schedule.getDayOfWeek().equals((short) i))
                            .map(ScheduleDto::getTimeInterval)
                            .toList();
                    scheduleList.add(new ScheduleDto(null, null, (short)i, null, intervals));
                });
        calendar.setSchedules(scheduleList);
        return calendar;
    }
}
