package ru.avdeev.scheduleservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.entity.Price;

import java.util.UUID;

public interface PriceRepository extends ReactiveCrudRepository<Price, UUID> {

    @Query(priceQuery)
    Mono<Double> getPrice(UUID resourceId, Double count);

    String priceQuery = """
            select
                price
            from price
            where
                resource_id = :resourceId and
                rate = (
                    select
                        max(rate)
                    from price
                    where
                        resource_id = :resourceId and
                        rate <= :count
                    )
            """;
}
