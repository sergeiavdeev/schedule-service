package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DebtDto;
import ru.avdeev.scheduleservice.entity.Debt;

import java.util.UUID;

public interface DebtService {
    Mono<Debt> saveDebt(UUID orderId, Double amountDt, Double amountKt);
    Mono<Void> deleteByOrderId(UUID orderId);
    Flux<DebtDto> getDebtsByOrderId(UUID orderId);
    Mono<Double> getDebt(UUID orderId);
    Mono<Double> getAmount(UUID orderId);
}
