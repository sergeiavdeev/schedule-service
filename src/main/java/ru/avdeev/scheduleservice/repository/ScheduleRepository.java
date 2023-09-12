package ru.avdeev.scheduleservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.avdeev.scheduleservice.entity.Schedule;

import java.util.UUID;

@Repository
public interface ScheduleRepository extends ReactiveCrudRepository<Schedule, UUID> {

    Flux<Schedule> findAllByCalendarIdOrderByDayOfWeekAscStartTimeAsc(UUID calendarId);
}
