package ru.avdeev.scheduleservice.service;

import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.UserDto;

import java.util.UUID;

public interface UserService {

    Mono<UserDto> findById(UUID id);
}
