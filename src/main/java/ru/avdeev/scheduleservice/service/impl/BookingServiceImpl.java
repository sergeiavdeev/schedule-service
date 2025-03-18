package ru.avdeev.scheduleservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.*;
import ru.avdeev.scheduleservice.exception.InvalidTimeIntervalException;
import ru.avdeev.scheduleservice.mapper.DebtMapper;
import ru.avdeev.scheduleservice.mapper.BookingMapper;
import ru.avdeev.scheduleservice.repository.BookingRepository;
import ru.avdeev.scheduleservice.service.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final DebtService debtService;
    private final PriceService priceService;
    private final BookingMapper bookingMapper;
    private final DebtMapper debtMapper;
    private final WorkTimeService workTimeService;
    private final Keycloak keycloak;
    //private final MessageService messageService;
    private final UserService userService;

    @Override
    @Transactional
    public Mono<BookingDto> save(BookingDto bookingDto) {

        if (bookingDto.getStartTime().isAfter(bookingDto.getEndTime()) ||
                bookingDto.getStartTime().equals(bookingDto.getEndTime())) {
            String msg = String.format("Дата начала интервала %s должна быть меньше даты окончания %s",
                    bookingDto.getStartTime(),
                    bookingDto.getEndTime()
            );
            throw new InvalidTimeIntervalException(msg);
        }

        return isWorkTime(bookingDto)
                .flatMap(isWorkTime -> {
                    if (isWorkTime) {
                        return  bookingRepository.exists(
                                        bookingDto.getResourceId(),
                                        bookingDto.getBookingDate(),
                                        bookingDto.getStartTime(),
                                        bookingDto.getEndTime()
                                )
                                .doOnNext(order -> {
                                    String msg = String.format("Желаемое время уже занято: %s - %s",
                                            bookingDto.getStartTime(),
                                            bookingDto.getEndTime()
                                    );
                                    throw new InvalidTimeIntervalException(msg);
                                })
                                .switchIfEmpty(bookingRepository.save(bookingMapper.toEntity(bookingDto)))
                                .map(bookingMapper::toDto)
                                .flatMap(this::saveDebt)
                                //.flatMap(order -> messageService.send("BookingCreated", "exch.booking", order))
                                //.map(o -> (OrderDto) o)
                                ;
                    }
                    String msg = String.format("Желаемое время не соответствует рабочему времени: %s - %s",
                            bookingDto.getStartTime(),
                            bookingDto.getEndTime()
                        );
                    return Mono.error(new InvalidTimeIntervalException(msg));
                });
    }

    @Override
    public Flux<BookingDto> findAll() {

        List<UserRepresentation> users = keycloak.realm("ttc-tops").users().list();

        return bookingRepository.findAll()
                .map(bookingMapper::toDto)
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
    public Mono<BookingDto> findById(UUID id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toDto);
    }

    @Override
    public Flux<BookingDto> findByUser(UUID userId) {
        return bookingRepository.findByUser(userId)
                .map(bookingMapper::toDto)
                .flatMapSequential(this::setDebt)
                .flatMapSequential(this::setAmount);
    }

    @Override
    public Flux<BookingDto> findAllAfterCurrentDate() {

        List<UserRepresentation> users = keycloak.realm("ttc-tops").users().list();

        List<UUID> usersIds = users.stream()
                .map(el -> UUID.fromString(el.getId()))
                .toList();

        return bookingRepository.findAfterCurrentDate(usersIds)
                .map(bookingMapper::toDto)
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
    public Mono<UUID> deleteById(UUID id) {
        return bookingRepository.deleteById(id)
                .then(debtService.deleteByOrderId(id))
                .then(Mono.just(id));
    }

    @Override
    public Mono<DebtDto> pay(UUID orderId, Double sum) {
        return debtService.saveDebt(orderId, 0D, sum)
                .map(debtMapper::toDto);
    }

    @Override
    public Flux<BookingDto> findAllByResourcesAfterCurrentDate(List<UUID> resources) {
        return bookingRepository.findByResourcesAfterCurrentDate(resources)
                .map(bookingMapper::toDto)
                .flatMapSequential(this::setUser)
                .flatMapSequential(this::setDebt)
                .flatMapSequential(this::setAmount);
    }

    private Mono<Boolean> isWorkTime(BookingDto order) {

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

    private Mono<BookingDto> saveDebt(BookingDto bookingDto) {

        return priceService.getAmount(bookingDto.getResourceId(), bookingDto.getCount())
                .flatMap(amount -> debtService.saveDebt(bookingDto.getId(), amount, 0D))
                .map(debt -> {
                    bookingDto.setAmount(debt.dt());
                    return bookingDto;
                });
    }

    private Mono<BookingDto> setDebt(BookingDto bookingDto) {

        return debtService.getDebt(bookingDto.getId())
                .map(debt -> {
                    bookingDto.setDebt(debt);
                    return bookingDto;
                });
    }

    private Mono<BookingDto> setAmount(BookingDto bookingDto) {

        return debtService.getAmount(bookingDto.getId())
                .map(amount -> {
                    bookingDto.setAmount(amount);
                    return bookingDto;
                });
    }

    private Mono<BookingDto> setUser(BookingDto bookingDto) {

        return userService.findById(bookingDto.getUserId())
                .map(user -> {
                    bookingDto.setUser(user);
                    return bookingDto;
                });
    }
}