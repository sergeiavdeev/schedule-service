package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DebtDto;
import ru.avdeev.scheduleservice.entity.Debt;
import ru.avdeev.scheduleservice.mapper.DebtMapper;
import ru.avdeev.scheduleservice.repository.DebtRepository;
import ru.avdeev.scheduleservice.service.DebtService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final DebtMapper debtMapper;

    @Override
    public Mono<Debt> saveDebt(UUID orderId, Double amountDt, Double amountKt) {
        return debtRepository.save(new Debt(null, null, orderId, amountDt, amountKt));
    }

    @Override
    public Mono<Void> deleteByOrderId(UUID orderId) {
        return debtRepository.deleteByOrderId(orderId);
    }

    @Override
    public Flux<DebtDto> getDebtsByOrderId(UUID orderId) {
        return debtRepository.findByOrderId(orderId)
                .map(debtMapper::toDto);
    }

    @Override
    public Mono<Double> getDebt(UUID orderId) {
        return debtRepository.getDebt(orderId);
    }

    @Override
    public Mono<Double> getAmount(UUID orderId) {
        return debtRepository.getAmount(orderId);
    }
}
