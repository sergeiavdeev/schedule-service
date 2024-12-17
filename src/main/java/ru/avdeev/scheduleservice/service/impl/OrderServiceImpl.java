package ru.avdeev.scheduleservice.service.impl;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DateWorkTimeDto;
import ru.avdeev.scheduleservice.dto.OrderDto;
import ru.avdeev.scheduleservice.dto.UserDto;
import ru.avdeev.scheduleservice.exception.InvalidTimeIntervalException;
import ru.avdeev.scheduleservice.mapper.OrderMapper;
import ru.avdeev.scheduleservice.repository.OrderRepository;
import ru.avdeev.scheduleservice.service.OrderService;
import ru.avdeev.scheduleservice.service.WorkTimeService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WorkTimeService workTimeService;
    private final Keycloak keycloak;

    @Override
    @Transactional
    public Mono<OrderDto> save(OrderDto orderDto) {

        return isWorkTime(orderDto)
                .flatMap(isWorkTime -> {
                    if (isWorkTime) {
                        return  orderRepository.exists(
                                        orderDto.getResourceId(),
                                        orderDto.getBookingDate(),
                                        orderDto.getStartTime(),
                                        orderDto.getEndTime()
                                )
                                .doOnNext(order -> {
                                    String msg = String.format("Желаемое время уже занято: %s - %s",
                                            orderDto.getStartTime(),
                                            orderDto.getEndTime()
                                    );
                                    throw new InvalidTimeIntervalException(msg);
                                })
                                .switchIfEmpty(orderRepository.save(orderMapper.toEntity(orderDto)))
                                .map(orderMapper::toDto);
                    }
                    String msg = String.format("Желаемое время не соответствует рабочему времени: %s - %s",
                            orderDto.getStartTime(),
                            orderDto.getEndTime()
                    );
                    return Mono.error(new InvalidTimeIntervalException(msg));
                });
    }

    @Override
    public Flux<OrderDto> findAll() {

        List<UserRepresentation> users = keycloak.realm("ttc-tops").users().list();

        return orderRepository.findAll()
                .map(orderMapper::toDto)
                .map(orderDto -> {
                            users.stream()
                                    .filter(el -> el.getId().equals(orderDto.getUserId().toString()))
                                    .findFirst().ifPresent(user -> orderDto.setUser(
                                            UserDto.builder()
                                                    .firstName(user.getFirstName())
                                                    .lastName(user.getLastName())
                                                    .email(user.getEmail())
                                                    .build()
                                    ));
                            return orderDto;
                        }
                );
    }

    @Override
    public Flux<OrderDto> findByUser(UUID userId) {
        return orderRepository.findByUser(userId)
                .map(orderMapper::toDto);
    }

    @Override
    @RolesAllowed("ROLE_ADMIN")
    public Flux<OrderDto> findAllAfterCurrentDate() {

        List<UserRepresentation> users = keycloak.realm("ttc-tops").users().list();

        List<UUID> usersIds = users.stream()
                .map(el -> UUID.fromString(el.getId()))
                .toList();

        return orderRepository.findAfterCurrentDate(usersIds)
                .map(orderMapper::toDto)
                .map(orderDto -> {
                            users.stream()
                                    .filter(el -> el.getId().equals(orderDto.getUserId().toString()))
                                    .findFirst().ifPresent(user -> orderDto.setUser(
                                            UserDto.builder()
                                                    .firstName(user.getFirstName())
                                                    .lastName(user.getLastName())
                                                    .email(user.getEmail())
                                                    .build()
                                    ));
                            return orderDto;
                        }
                );
    }

    private Mono<Boolean> isWorkTime(OrderDto order) {

        return workTimeService.getWorkTime(order.getStorageId(), order.getBookingDate(), order.getBookingDate())
                .map(workTimeDto -> {
                    DateWorkTimeDto workTime = workTimeDto.getDateWorkTimeList().stream().findFirst().orElse(null);
                    boolean isWorkTime = false;
                    if (workTime != null) {
                        isWorkTime = workTime.getTimeIntervals().stream().anyMatch(interval ->
                                (order.getStartTime().isAfter(interval.getStartTime()) ||
                                order.getStartTime().equals(interval.getStartTime()))
                                &&
                                (order.getEndTime().isBefore(interval.getEndTime()) ||
                                        order.getEndTime().equals(interval.getEndTime()))
                        );
                    }
                    return isWorkTime;
                });
    }
}