package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.result.view.Rendering;
import ru.yandex.practicum.dto.request.OrderRequest;
import ru.yandex.practicum.service.OrderService;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.utils.RenderingUtils;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Secured("ROLE_USER")
    @GetMapping("orders/{id}")
    public Mono<Rendering> findById(@PathVariable Long id, @ModelAttribute OrderRequest orderRequest) {
        return orderService.findById(id)
                .map(order -> Rendering.view("order")
                        .modelAttribute("order", order)
                        .modelAttribute("newOrder", orderRequest.getNewOrder())
                        .build());
    }

    @Secured("ROLE_USER")
    @GetMapping("orders")
    public Mono<Rendering> findAll() {
        return orderService.findAll()
                .collectList()
                .map(orders -> Rendering.view("orders")
                        .modelAttribute("orders", orders)
                        .build());
    }

    @Secured("ROLE_USER")
    @PostMapping ("buy")
    public Mono<Rendering> buy() {
        return orderService.buy()
                .map(order -> String.format("/orders/%d?newOrder=true", order.getId()))
                .map(RenderingUtils::redirectTo);
    }
}