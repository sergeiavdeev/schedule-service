package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DeviationDto;
import ru.avdeev.scheduleservice.dto.TimeIntervalDto;
import ru.avdeev.scheduleservice.entity.Deviation;
import ru.avdeev.scheduleservice.repository.DeviationRepository;
import ru.avdeev.scheduleservice.service.DeviationService;
import ru.avdeev.scheduleservice.utils.DateUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviationServiceImpl implements DeviationService {

    private final DeviationRepository repository;

    @Override
    @Transactional
    public Mono<DeviationDto> add(DeviationDto deviation, UUID calendarId) {

        DateUtils.checkIntervals(deviation.getTimeIntervals());

        return repository.deleteByCalendarIdAndDate(calendarId, deviation.getDate())
                .then(Flux.fromIterable(deviation.getTimeIntervals())
                        .switchIfEmpty(Flux.fromIterable(List.of(new TimeIntervalDto(LocalTime.MIN, LocalTime.MIN)))
                        )
                        .map(timeInterval -> new Deviation(
                                null,
                                calendarId,
                                deviation.getDate(),
                                timeInterval.getStartTime(),
                                timeInterval.getEndTime())
                        ).collectList()
                        .flatMap(dev -> repository.saveAll(dev)
                                .collectList()
                                .map(devList -> {
                                    List<TimeIntervalDto> intervals = new ArrayList<>();
                                    for (Deviation el : devList) {
                                        intervals.add(new TimeIntervalDto(el.startTime(), el.endTime()));
                                    }
                                    return DeviationDto.builder()
                                            .id(devList.getFirst().id())
                                            .calendarId(calendarId)
                                            .date(devList.getFirst().date())
                                            .timeIntervals(intervals)
                                            .build();
                                }))
                )
                ;
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Void> delete(UUID calendarId, LocalDate date) {
        return repository.deleteByCalendarIdAndDate(calendarId, date);
    }

    @Override
    public Flux<DeviationDto> getByDateInterval(UUID calendarId, LocalDate startDate, LocalDate endDate) {

         return repository.findAllByCalendarIdAndDateBetweenOrderByDateAscStartTimeAsc(calendarId, startDate, endDate)
                 .groupBy(Deviation::date)
                 .flatMap(localDateDeviationGroupedFlux -> localDateDeviationGroupedFlux.collectList()
                         .map(deviations -> DeviationDto.builder()
                                 .date(deviations.getFirst().date())
                                 .calendarId(calendarId)
                                 .timeIntervals(
                                         deviations.stream()
                                                 .map(deviation -> new TimeIntervalDto(deviation.startTime(), deviation.endTime()))
                                                 .toList()
                                 )
                                 .build()));
    }
}
