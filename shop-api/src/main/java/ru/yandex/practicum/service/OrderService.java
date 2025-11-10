package ru.yandex.practicum.service;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.dto.response.OrderInfo;

public interface OrderService {
    Mono<OrderInfo> findById(Long id);
    Flux<OrderInfo> findAll();
    Mono<OrderInfo> buy(Long userId);
}