package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.*;
import ru.avdeev.scheduleservice.mapper.CalendarMapper;
import ru.avdeev.scheduleservice.repository.CalendarRepository;
import ru.avdeev.scheduleservice.service.CalendarService;
import ru.avdeev.scheduleservice.service.DeviationService;
import ru.avdeev.scheduleservice.service.ScheduleService;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository repository;
    private final CalendarMapper mapper;

    private final ScheduleService scheduleService;
    private final DeviationService deviationService;
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

    @Override
    public Mono<WorkTimeDto> getWorkTime(UUID ownerId, LocalDate startDate, LocalDate endDate) {

        return getByOwner(ownerId)
                .flatMap(calendarDto -> deviationService.getByDateV2(calendarDto.getId(), startDate, endDate)
                        .collectList()
                        .zipWith(Mono.just(calendarDto)))
                .flatMap(t -> getWorkTimeWithDeviations(t.getT2(), t.getT1(), startDate, endDate));
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

    private Mono<WorkTimeDto> getWorkTimeWithDeviations(
            CalendarDto calendar, List<DeviationDto> deviations, LocalDate startDate, LocalDate endDate) {

        WorkTimeDto workTime = new WorkTimeDto(calendar.getOwnerId(), calendar.getId(), new ArrayList<>());

        return Flux.fromIterable(startDate.datesUntil(endDate.plusDays(1)).toList())
                .map(date -> new DateWorkTimeDto(date, date.getDayOfWeek().getValue(), null))
                .map(dateWorkTime -> setTimeIntervals(dateWorkTime, calendar, deviations))
                .collectList()
                .map(dateWorkTimes -> {
                    workTime.setDateWorkTimeList(dateWorkTimes);
                    return workTime;
                });
    }

    private DateWorkTimeDto setTimeIntervals(DateWorkTimeDto dateWorkTime, CalendarDto calendar, List<DeviationDto> deviations) {

        DeviationDto deviationByDate = deviations.stream()
                .filter(deviation -> deviation.getDate().equals(dateWorkTime.getDate()))
                .findFirst().orElse(null);

        List<TimeIntervalDto> intervalsFromSchedule = calendar.getSchedules().stream()
                .filter(schedule -> schedule.getDayOfWeek().equals(dateWorkTime.getDayOfWeek()))
                .findFirst()
                .orElse(new ScheduleDto())
                .getTimeIntervals();

        if (deviationByDate != null) {
            dateWorkTime.setTimeIntervals(deviationByDate.getTimeIntervals());
        } else {
            dateWorkTime.setTimeIntervals(intervalsFromSchedule);
        }

        return dateWorkTime;
    }
}