package ru.avdeev.scheduleservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.entity.Debt;

import java.util.UUID;

public interface DebtRepository  extends ReactiveCrudRepository<Debt, UUID> {

    Mono<Void> deleteByOrderId(UUID orderId);
    Flux<Debt> findByOrderId(UUID orderId);

    @Query(debtQuery)
    Mono<Double> getDebt(UUID orderId);

    @Query(amountQuery)
    Mono<Double> getAmount(UUID orderId);

    String debtQuery = """    
        select sum(dt - kt) as debt 
        from debt
        where order_id = :orderId
        group by order_id;
    """;

    String amountQuery = """
        select sum(dt) as debt 
        from debt
        where order_id = :orderId
        group by order_id;
    """;
}
