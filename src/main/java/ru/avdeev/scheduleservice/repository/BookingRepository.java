package ru.avdeev.scheduleservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.entity.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends ReactiveCrudRepository<Booking, UUID> {

    @Query(existsQuery)
    Mono<Booking> exists(UUID resourceId, LocalDate orderDate, LocalTime startTime, LocalTime endTime);

    Flux<Booking> findAllByResourceIdAndBookingDate(UUID resourceId, LocalDate bookingDate);

    @Query(userQuery)
    Flux<Booking> findByUser(UUID userId);

    @Query(adminQuery)
    Flux<Booking> findAfterCurrentDate(List<UUID> userIds);

    @Query(allQuery)
    Flux<Booking> findByResourcesAfterCurrentDate(List<UUID> resourceIds);

    String existsQuery = """
            select *
            from schedule.booking
            where
                booking_date = :orderDate AND
                resource_id = :resourceId AND
                (:startTime + interval '1 Minutes'  between start_time and end_time OR
                :endTime - interval '1 Minutes' between start_time and end_time)
            limit 1
    """;

    String userQuery = """
        select *
        from booking
        where
            booking_date >= date(timezone('UTC+03', now()::timestamp)) and
            user_id = :userId
        order by booking_date, start_time
    """;

    String adminQuery = """    
        select * from booking
        where
            booking_date >= date(timezone('UTC+03', now()::timestamp)) and
            user_id in (:userIds)
        order by booking_date, start_time
    """;

    String allQuery = """    
        select * from booking
        where
            booking_date >= date(timezone('UTC+03', now()::timestamp)) and
            resource_id in (:resourceIds)
        order by booking_date, start_time
    """;
}
