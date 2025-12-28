package ru.yandex.practicum.repository;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.model.CartItem;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

@Repository
public interface CartItemRepository extends R2dbcRepository<CartItem, Long> {
    Mono<CartItem> findByItemIdAndUserIdAndOrderIdIsNull(Long itemId, Long userId);
    Flux<CartItem> findAllByOrderIdAndUserId(Long orderId, Long userId);
    Flux<CartItem> findAllByUserIdAndOrderIdIsNull(Long userId);
    Mono<Void> deleteByItemIdAndUserId(Long itemId, Long userId);
}