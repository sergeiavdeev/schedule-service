package ru.avdeev.scheduleservice.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.UserDto;
import ru.avdeev.scheduleservice.service.UserService;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Keycloak keycloak;

    @Override
    public Mono<UserDto> findById(UUID id) {
        //UserRepresentation user = keycloak.realm("ttc-tops").users().get(id.toString()).toRepresentation();
        return Mono.fromCallable(() -> keycloak.realm("ttc-tops").users().get(id.toString()).toRepresentation())
                .map(user -> UserDto.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build());

    }
}
