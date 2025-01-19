package ru.avdeev.scheduleservice.controller;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.DebtDto;
import ru.avdeev.scheduleservice.dto.OrderDto;
import ru.avdeev.scheduleservice.exception.ApiException;
import ru.avdeev.scheduleservice.service.OrderService;
import ru.avdeev.scheduleservice.service.PriceService;

import java.util.UUID;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PriceService priceService;

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
    public Mono<Double> getPrice(@RequestParam UUID resourceId, @RequestParam Double count) {
        return priceService.getAmount(resourceId, count);
    }

    @PostMapping("")
    public Mono<OrderDto> createOrder(@RequestBody OrderDto orderDto, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaim("sub").toString());
        orderDto.setUserId(userId);
        return orderService.save(orderDto);
    }

    @PostMapping("/pay")
    public Mono<DebtDto> pay(@RequestBody DebtDto debt) {
        return orderService.pay(debt.getOrderId(), debt.getKt());
    }

    @DeleteMapping
    public Mono<Void> deleteOrder(@RequestBody OrderDto orderDto) {
        return orderService.deleteById(orderDto.getId());
    }
}
