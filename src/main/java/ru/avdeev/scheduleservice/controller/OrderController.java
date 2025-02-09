package ru.avdeev.scheduleservice.controller;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.configuration.RabbitConfig;
import ru.avdeev.scheduleservice.dto.DebtDto;
import ru.avdeev.scheduleservice.dto.OrderDto;
import ru.avdeev.scheduleservice.exception.ApiException;
import ru.avdeev.scheduleservice.service.OrderService;
import ru.avdeev.scheduleservice.service.PriceService;
import ru.avdeev.scheduleservice.service.impl.MessageService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PriceService priceService;
    private final MessageService messageService;

    @GetMapping("")
    public Flux<OrderDto> getOrders(@RequestParam boolean admin, @AuthenticationPrincipal Jwt jwt) {

        if (jwt == null) {
            throw new ApiException(HttpResponseStatus.UNAUTHORIZED, "Unauthorized");
        }
        if (admin) {
            return orderService.findAllAfterCurrentDate();
        }
        UUID userId = UUID.fromString(jwt.getClaim("sub").toString());
        return orderService.findByUser(userId);
    }

    @GetMapping("/price")
    public Mono<String> getPrice(@RequestParam UUID resourceId, @RequestParam Double count) {
        return priceService.getAmount(resourceId, count)
                .map(amount -> String.format("%.0f", amount));
    }

    @PostMapping("")
    public Mono<OrderDto> createOrder(@RequestBody OrderDto orderDto, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("sub").toString());
        orderDto.setUserId(userId);
        return orderService.save(orderDto)
                .flatMap(order -> messageService.send("BookingCreated", RabbitConfig.BOOKING_EXCHANGE, order))
                .map(o -> (OrderDto) o);
    }

    @PostMapping("/pay")
    public Mono<DebtDto> pay(@RequestBody DebtDto debt) {
        return orderService.pay(debt.getOrderId(), debt.getKt())
                .flatMap(debtDto -> messageService.send("OrderPayed", RabbitConfig.BOOKING_EXCHANGE, debtDto))
                .map(o -> (DebtDto) o);
    }

    @DeleteMapping
    public Mono<OrderDto> deleteOrder(@RequestBody OrderDto orderDto) {
        return orderService.findById(orderDto.getId())
                .zipWith(orderService.deleteById(orderDto.getId()))
                .flatMap(t -> messageService.send("BookingCanceled", RabbitConfig.BOOKING_EXCHANGE, t.getT1()))
                .map(o -> (OrderDto) o);
    }
}
