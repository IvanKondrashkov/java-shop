package ru.yandex.practicum.service;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.dto.response.OrderInfo;
import ru.yandex.practicum.dto.response.CartItemInfo;

public interface CartItemService {
    Flux<ItemInfo> findAll();
    Flux<CartItemInfo> findAllByOrderId(Long id);
    Mono<ItemInfo> purchaseItem(Long id, Action action);
    Mono<OrderInfo> purchaseOrder();
    Mono<Void> deleteById(Long id, Action action);
}