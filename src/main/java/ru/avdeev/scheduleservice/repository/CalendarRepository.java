package ru.avdeev.scheduleservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.entity.Calendar;

import java.util.UUID;

public interface CalendarRepository extends ReactiveCrudRepository<Calendar, UUID> {

    Mono<Calendar> findTop1ByOwnerIdOrderByStartDateDesc(UUID ownerId);
}
