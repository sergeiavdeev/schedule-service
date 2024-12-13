package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.*;
import ru.avdeev.scheduleservice.exception.ResourceNotFoundException;
import ru.avdeev.scheduleservice.mapper.OrderMapper;
import ru.avdeev.scheduleservice.repository.OrderRepository;
import ru.avdeev.scheduleservice.service.CalendarService;
import ru.avdeev.scheduleservice.service.DeviationService;
import ru.avdeev.scheduleservice.service.WorkTimeService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class WorkTimeServiceImpl implements WorkTimeService {

    private final CalendarService calendarService;
    private final DeviationService deviationService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public Mono<WorkTimeDto> getWorkTime(UUID ownerId, LocalDate startDate, LocalDate endDate) {

        Comparator<CalendarDto> dateComparator = Comparator
                .comparing(CalendarDto::getStartDate, Comparator.reverseOrder());

        AtomicReference<LocalDate> lEndDate = new AtomicReference<>(endDate);

        return calendarService.getAllByOwner(ownerId, startDate)
                .filter(calendarDto ->
                        calendarDto.getStartDate().isBefore(endDate) || calendarDto.getStartDate().isEqual(endDate))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Not calendars found by owner id: %s", ownerId)))
                .flatMap(calendarDto -> deviationService.getByDateInterval(calendarDto.getId(), startDate, endDate)
                        .collectList()
                        .zipWith(Mono.just(calendarDto)))
                .sort((s1, s2) -> dateComparator.compare(s1.getT2(), s2.getT2()))
                .flatMap(t2 -> {
                    CalendarDto calendar = t2.getT2();
                    List<DeviationDto> deviations = t2.getT1();

                    LocalDate lStartDate = startDate;
                    if (calendar.getStartDate().isAfter(startDate)) {
                        lStartDate = calendar.getStartDate();
                    }
                    Mono<WorkTimeDto> wt = getWorkTimeWithDeviations(calendar, deviations, lStartDate, lEndDate.get());
                    lEndDate.set(lStartDate.minusDays(1));
                    return wt;
                })
                .flatMap(workTimeDto -> Flux.fromIterable(workTimeDto.getDateWorkTimeList()))
                .sort(Comparator.comparing(DateWorkTimeDto::getDate))
                .collectList()
                .map(dateWorkTimeList -> new WorkTimeDto(ownerId, dateWorkTimeList));
    }

    @Override
    public Mono<WorkTimeDto> getFreeWorkTime(UUID storageId, UUID resourceId, LocalDate date) {
        return getWorkTime(storageId, date, date)
                .map(workTimeDto -> workTimeDto.getDateWorkTimeList().getFirst().getTimeIntervals())
                .zipWith(orderRepository.findAllByResourceIdAndBookingDate(resourceId, date).map(orderMapper::toDto).collectList())
                .map(t -> concatIntervals(t.getT1(), t.getT2()))
                .map(timeIntervals -> {
                    DateWorkTimeDto dateWorkTimeDto = new DateWorkTimeDto(date, date.getDayOfWeek().getValue(), timeIntervals);
                    return new WorkTimeDto(storageId, List.of(dateWorkTimeDto));
                })
                ;
    }

    private Mono<WorkTimeDto> getWorkTimeWithDeviations(CalendarDto calendar, List<DeviationDto> deviations,
                                                        LocalDate startDate, LocalDate endDate) {

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

    private DateWorkTimeDto setTimeIntervals(DateWorkTimeDto dateWorkTime, CalendarDto calendar,
                                             List<DeviationDto> deviations) {

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
    private List<TimeIntervalDto> concatIntervals(List<TimeIntervalDto> free, List<OrderDto> busy) {

        busy.sort((el1, el2) -> {
            if (el1.getStartTime().isBefore(el2.getStartTime())) {
                return -1;
            } else if (el1.getStartTime().isAfter(el2.getStartTime())) {
                return 1;
            }
            return 0;
        });

        ArrayList<TimeIntervalDto> result = new ArrayList<>();
        free.forEach(freeInterval -> {
            List<OrderDto> busyIntervals = busy.stream()
                    .filter(el ->
                            (el.getEndTime().isBefore(freeInterval.getEndTime()) ||
                                    el.getEndTime().equals(freeInterval.getEndTime()))
                            &&
                            (el.getStartTime().isAfter(freeInterval.getStartTime()) ||
                                    el.getStartTime().equals(freeInterval.getStartTime()))
                    )
                    .toList();

            LocalTime startTime = freeInterval.getStartTime();

            for (OrderDto busyInterval : busyIntervals) {
                result.add(new TimeIntervalDto(startTime, busyInterval.getStartTime()));
                startTime = busyInterval.getEndTime();
            }
            result.add(new TimeIntervalDto(startTime, freeInterval.getEndTime()));
        });
        return result.stream().filter(el -> !el.getStartTime().equals(el.getEndTime())).toList();
    }
}
