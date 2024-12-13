package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.WorkTimeDto;

import java.time.LocalDate;
import java.util.UUID;

public interface WorkTimeService {

    Mono<WorkTimeDto> getWorkTime(UUID ownerId, LocalDate startDate, LocalDate endDate);
    Mono<WorkTimeDto> getFreeWorkTime(UUID storageId, UUID resourceId, LocalDate startDate);
}
