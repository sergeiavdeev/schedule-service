package ru.avdeev.scheduleservice.controller;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.configuration.RabbitConfig;
import ru.avdeev.scheduleservice.dto.BookingDto;
import ru.avdeev.scheduleservice.dto.DebtDto;
import ru.avdeev.scheduleservice.exception.ApiException;
import ru.avdeev.scheduleservice.service.BookingService;
import ru.avdeev.scheduleservice.service.PriceService;
import ru.avdeev.scheduleservice.service.impl.MessageService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final PriceService priceService;
    private final MessageService messageService;

    @GetMapping("")
    public Flux<BookingDto> getOrders(@RequestParam boolean admin, @AuthenticationPrincipal Jwt jwt) {

        if (jwt == null) {
            throw new ApiException(HttpResponseStatus.UNAUTHORIZED, "Unauthorized");
        }
        if (admin) {
            return bookingService.findAllAfterCurrentDate();
        }
        UUID userId = UUID.fromString(jwt.getClaim("sub").toString());
        return bookingService.findByUser(userId);
    }

    @GetMapping("/all")
    public Flux<BookingDto> getAllOrders(@RequestParam List<UUID> resources) {
        return bookingService.findAllByResourcesAfterCurrentDate(resources);
    }

    @GetMapping("/price")
    public Mono<String> getPrice(@RequestParam UUID resourceId, @RequestParam Double count) {
        return priceService.getAmount(resourceId, count)
                .map(amount -> String.format("%.0f", amount));
    }

    @PostMapping("")
    public Mono<BookingDto> createOrder(@RequestBody BookingDto bookingDto, @AuthenticationPrincipal Jwt jwt) {
        log.info("Receive request: {}", bookingDto);
        UUID userId = UUID.fromString(jwt.getClaim("sub").toString());
        bookingDto.setUserId(userId);
        return bookingService.save(bookingDto)
                .flatMap(order -> messageService.send("BookingCreated", RabbitConfig.BOOKING_EXCHANGE, order))
                .map(o -> (BookingDto) o);
    }

    @PostMapping("/pay")
    public Mono<DebtDto> pay(@RequestBody DebtDto debt) {
        return bookingService.pay(debt.getOrderId(), debt.getKt())
                .flatMap(debtDto -> messageService.send("OrderPayed", RabbitConfig.BOOKING_EXCHANGE, debtDto))
                .map(o -> (DebtDto) o);
    }

    @DeleteMapping
    public Mono<BookingDto> deleteOrder(@RequestBody BookingDto bookingDto) {
        return bookingService.findById(bookingDto.getId())
                .zipWith(bookingService.deleteById(bookingDto.getId()))
                .flatMap(t -> messageService.send("BookingCanceled", RabbitConfig.BOOKING_EXCHANGE, t.getT1()))
                .map(o -> (BookingDto) o);
    }
}
