package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.ResourceDto;
import ru.avdeev.scheduleservice.service.ResourceService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final WebClient gatewayClient;

    @Override
    public Mono<ResourceDto> getById(UUID id) {

        String resourceUrl = String.format("/api/v1/resource/%s", id);
        return gatewayClient.get()
                .uri(resourceUrl)
                .retrieve()
                .bodyToMono(ResourceDto.class);
    }
}
