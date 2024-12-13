package ru.avdeev.scheduleservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.entity.Order;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, UUID> {

    @Query(query)
    Mono<Order> exists(UUID resourceId, LocalDate orderDate, LocalTime startTime, LocalTime endTime);

    Flux<Order> findAllByResourceIdAndBookingDate(UUID resourceId, LocalDate bookingDate);
    
    String query = """
            select *
            from schedule.booking
            where
                booking_date = :orderDate AND
                resource_id = :resourceId AND
                (:startTime + interval '1 Minutes'  between start_time and end_time OR
                :endTime - interval '1 Minutes' between start_time and end_time)
            limit 1""";
}
