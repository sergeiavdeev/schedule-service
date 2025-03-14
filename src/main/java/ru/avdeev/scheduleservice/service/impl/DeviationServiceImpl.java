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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviationServiceImpl implements DeviationService {

    private final DeviationRepository repository;

    @Override
    @Transactional
    public Mono<Void> add(DeviationDto deviation, UUID calendarId) {

        DateUtils.checkIntervals(deviation.getTimeIntervals());

        return repository.deleteByCalendarIdAndDate(calendarId, deviation.getDate())
                .then(Flux.fromIterable(deviation.getTimeIntervals())
                        .switchIfEmpty(Flux.fromIterable(List.of(new TimeIntervalDto(null, null)))
                        )
                        .map(timeInterval -> new Deviation(
                                null,
                                calendarId,
                                deviation.getDate(),
                                timeInterval.getStartTime(),
                                timeInterval.getEndTime())
                        ).collectList()
                        .flatMap(dev -> repository.saveAll(dev).then())
                );
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<DeviationDto> getByDateInterval(UUID calendarId, LocalDate startDate, LocalDate endDate) {

         return repository.findAllByCalendarIdAndDateBetweenOrderByDateAscStartTimeAsc(calendarId, startDate, endDate)
                 .groupBy(Deviation::date)
                 .flatMap(localDateDeviationGroupedFlux -> localDateDeviationGroupedFlux.collectList()
                         .map(deviations -> DeviationDto.builder()
                                 .id(deviations.getFirst().id())
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
