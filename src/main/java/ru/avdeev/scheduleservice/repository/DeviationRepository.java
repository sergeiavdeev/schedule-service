package ru.avdeev.scheduleservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.entity.Deviation;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface DeviationRepository extends ReactiveCrudRepository<Deviation, UUID> {

    Flux<Deviation> findAllByCalendarIdAndDateBetweenOrderByDateAscStartTimeAsc(
            UUID calendarId, LocalDate startDate, LocalDate endDate);

    Mono<Void> deleteByCalendarIdAndDate(UUID calendarId, LocalDate date);
}
