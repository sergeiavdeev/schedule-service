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
import ru.avdeev.scheduleservice.dto.DebtDto;
import ru.avdeev.scheduleservice.dto.OrderDto;
import ru.avdeev.scheduleservice.dto.UserDto;
import ru.avdeev.scheduleservice.exception.InvalidTimeIntervalException;
import ru.avdeev.scheduleservice.mapper.DebtMapper;
import ru.avdeev.scheduleservice.mapper.OrderMapper;
import ru.avdeev.scheduleservice.repository.OrderRepository;
import ru.avdeev.scheduleservice.service.DebtService;
import ru.avdeev.scheduleservice.service.OrderService;
import ru.avdeev.scheduleservice.service.PriceService;
import ru.avdeev.scheduleservice.service.WorkTimeService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DebtService debtService;
    private final PriceService priceService;
    private final OrderMapper orderMapper;
    private final DebtMapper debtMapper;
    private final WorkTimeService workTimeService;
    private final Keycloak keycloak;

    @Override
    @Transactional
    public Mono<OrderDto> save(OrderDto orderDto) {

        if (orderDto.getStartTime().isAfter(orderDto.getEndTime()) ||
                orderDto.getStartTime().equals(orderDto.getEndTime())) {
            String msg = String.format("Дата начала интервала %s должна быть меньше даты окончания %s",
                    orderDto.getStartTime(),
                    orderDto.getEndTime()
            );
            throw new InvalidTimeIntervalException(msg);
        }

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
                                .map(orderMapper::toDto)
                                .flatMap(this::saveDebt);
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
                .map(orderMapper::toDto)
                .flatMapSequential(this::setDebt)
                .flatMapSequential(this::setAmount);
    }

    @Override
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
                )
                .flatMapSequential(this::setDebt)
                .flatMapSequential(this::setAmount);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(UUID id) {
        return orderRepository.deleteById(id)
                .then(debtService.deleteByOrderId(id));
    }

    @Override
    public Mono<DebtDto> pay(UUID orderId, Double sum) {
        return debtService.saveDebt(orderId, 0D, sum)
                .map(debtMapper::toDto);
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

    private Mono<OrderDto> saveDebt(OrderDto orderDto) {

        return priceService.getAmount(orderDto.getResourceId(), orderDto.getCount())
                .flatMap(amount -> debtService.saveDebt(orderDto.getId(), amount, 0D))
                .map(debt -> {
                    orderDto.setAmount(debt.dt());
                    return orderDto;
                });
    }

    private Mono<OrderDto> setDebt(OrderDto orderDto) {

        return debtService.getDebt(orderDto.getId())
                .map(debt -> {
                    orderDto.setDebt(debt);
                    return orderDto;
                });
    }

    private Mono<OrderDto> setAmount(OrderDto orderDto) {

        return debtService.getAmount(orderDto.getId())
                .map(amount -> {
                    orderDto.setAmount(amount);
                    return orderDto;
                });
    }
}