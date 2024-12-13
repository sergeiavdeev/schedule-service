package ru.avdeev.scheduleservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.avdeev.scheduleservice.dto.OrderDto;
import ru.avdeev.scheduleservice.service.OrderService;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("")
    public Flux<OrderDto> getOrders() {
        return orderService.findAll();
    }

    @PostMapping("")
    public Mono<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        return orderService.save(orderDto);
    }
}
