package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.repository.PriceRepository;
import ru.avdeev.scheduleservice.service.PriceService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    @Override
    public Mono<Double> getAmount(UUID resourceId, Double count) {
        return priceRepository.getPrice(resourceId, count)
                .map(price -> price * count);
    }
}
