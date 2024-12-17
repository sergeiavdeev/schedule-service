package ru.avdeev.scheduleservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.entity.Order;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, UUID> {

    @Query(existsQuery)
    Mono<Order> exists(UUID resourceId, LocalDate orderDate, LocalTime startTime, LocalTime endTime);

    Flux<Order> findAllByResourceIdAndBookingDate(UUID resourceId, LocalDate bookingDate);

    @Query(userQuery)
    Flux<Order> findByUser(UUID userId);

    @Query(adminQuery)
    Flux<Order> findAfterCurrentDate(List<UUID> userIds);

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
            booking_date >= current_date and
            user_id = :userId
        order by booking_date, start_time
    """;

    String adminQuery = """    
        select * from booking
        where
            booking_date >= current_date and
            user_id in (:userIds)
        order by booking_date, start_time
    """;
}
