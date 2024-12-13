package ru.avdeev.scheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DateWorkTimeDto;
import ru.avdeev.scheduleservice.dto.OrderDto;
import ru.avdeev.scheduleservice.exception.InvalidTimeIntervalException;
import ru.avdeev.scheduleservice.mapper.OrderMapper;
import ru.avdeev.scheduleservice.repository.OrderRepository;
import ru.avdeev.scheduleservice.service.OrderService;
import ru.avdeev.scheduleservice.service.WorkTimeService;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WorkTimeService workTimeService;

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
        return orderRepository.findAll()
                .map(orderMapper::toDto);
    }

    @Override
    public Flux<OrderDto> findByResourceIdAndDate(UUID resourceId, LocalDate date) {
        return orderRepository.findAllByResourceIdAndBookingDate(resourceId, date)
                .map(orderMapper::toDto);
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