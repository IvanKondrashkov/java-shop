package ru.yandex.practicum.repository;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.model.CartItem;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

@Repository
public interface CartItemRepository extends R2dbcRepository<CartItem, Long> {
    Mono<CartItem> findByItemIdAndOrderIdIsNull(Long itemId);
    Flux<CartItem> findAllByOrderId(Long orderId);
    Flux<CartItem> findAllByOrderIdIsNull();
    Mono<Void> deleteByItemId(Long itemId);
    @Query(value = "SELECT ci.quantity FROM cart_items ci WHERE ci.item_id = :itemId AND ci.order_id IS NULL")
    Mono<Integer> countByItemId(Long itemId);
}