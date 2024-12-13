package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.ResourceDto;

import java.util.UUID;

public interface ResourceService {

    Mono<ResourceDto> getById(UUID id);
}
