package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DeviationDto;
import ru.avdeev.scheduleservice.mapper.DeviationMapper;
import ru.avdeev.scheduleservice.repository.DeviationRepository;
import ru.avdeev.scheduleservice.service.DeviationService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DeviationServiceImpl implements DeviationService {

    private final DeviationRepository repository;
    private final DeviationMapper mapper;
    @Override
    public Flux<DeviationDto> getByDate(LocalDate startDate, LocalDate endDate) {
        return repository.findAllByDateBetweenOrderByDateAscStartTimeAsc(startDate, endDate)
                .map(mapper::toDto);
    }

    @Override
    public Mono<DeviationDto> add(DeviationDto deviation) {
        return repository.save(mapper.toEntity(deviation))
                .map(mapper::toDto);
    }
}
