package ru.avdeev.scheduleservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.entity.Calendar;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface CalendarRepository extends ReactiveCrudRepository<Calendar, UUID> {

    Mono<Calendar> findTop1ByOwnerIdOrderByStartDateDesc(UUID ownerId);
    Flux<Calendar> findAllByOwnerIdAndStartDateGreaterThanEqual(UUID ownerId, LocalDate startDate);
    Mono<Calendar> findTop1ByOwnerIdAndStartDateLessThanOrderByStartDateDesc(UUID ownerId, LocalDate startDate);
}
