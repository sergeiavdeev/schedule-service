package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.*;
import ru.avdeev.scheduleservice.service.CalendarService;
import ru.avdeev.scheduleservice.service.DeviationService;
import ru.avdeev.scheduleservice.service.WorkTimeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkTimeServiceImpl implements WorkTimeService {

    private final CalendarService calendarService;
    private final DeviationService deviationService;
    @Override
    public Mono<WorkTimeDto> getWorkTime(UUID ownerId, LocalDate startDate, LocalDate endDate) {
        return calendarService.getByOwner(ownerId)
                .flatMap(calendarDto -> deviationService.getByDateInterval(calendarDto.getId(), startDate, endDate)
                        .collectList()
                        .zipWith(Mono.just(calendarDto)))
                .flatMap(t -> getWorkTimeWithDeviations(t.getT2(), t.getT1(), startDate, endDate));
    }

    private Mono<WorkTimeDto> getWorkTimeWithDeviations(
            CalendarDto calendar, List<DeviationDto> deviations, LocalDate startDate, LocalDate endDate) {

        WorkTimeDto workTime = new WorkTimeDto(calendar.getOwnerId(), new ArrayList<>());

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
