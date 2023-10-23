package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.CalendarDto;
import ru.avdeev.scheduleservice.dto.ScheduleDto;
import ru.avdeev.scheduleservice.dto.TimeIntervalDto;
import ru.avdeev.scheduleservice.mapper.CalendarMapper;
import ru.avdeev.scheduleservice.repository.CalendarRepository;
import ru.avdeev.scheduleservice.service.CalendarService;
import ru.avdeev.scheduleservice.service.ScheduleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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
    public Flux<CalendarDto> getAllByOwner(UUID ownerId, LocalDate startDate) {
        return repository.findAllByOwnerIdAndStartDateGreaterThanEqual(ownerId, startDate)
                .concatWith(repository.findTop1ByOwnerIdAndStartDateLessThanOrderByStartDateDesc(ownerId, startDate))
                .map(mapper::toDto)
                .flatMap(this::setSchedules)
                .sort(Comparator.comparing(CalendarDto::getStartDate));
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
                            .filter(schedule -> schedule.getDayOfWeek().equals(i))
                            .map(ScheduleDto::getTimeInterval)
                            .toList();
                    scheduleList.add(new ScheduleDto(null, null, i, null, intervals));
                });
        calendar.setSchedules(scheduleList);
        return calendar;
    }
}