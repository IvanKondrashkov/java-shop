package ru.yandex.practicum.service;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.dto.response.OrderInfo;
import ru.yandex.practicum.dto.response.CartItemInfo;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.mapper.CartItemMapper;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.exception.EntityIsEmptyException;
import ru.yandex.practicum.exception.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemInfo> findAll() {
        return cartItemRepository.findAllByOrderIdIsNull()
                .flatMap(it -> Mono.zip(
                        itemRepository.findById(it.getItemId()),
                        imageRepository.findByItemId(it.getItemId()),
                        cartItemRepository.countByItemId(it.getItemId()).defaultIfEmpty(0))
                )
                .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CartItemInfo> findAllByOrderId(Long id) {
        return cartItemRepository.findAllByOrderId(id)
                .flatMap(it -> Mono.zip(
                        Mono.just(it),
                        itemRepository.findById(it.getItemId())
                )
                .map(tuple -> CartItemMapper.cartItemToCartItemInfo(tuple.getT1(), tuple.getT2())));
    }

    @Override
    public Mono<ItemInfo> purchaseItem(Long id, Action action) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Item not found!")))
                .flatMap(item -> cartItemRepository.findByItemIdAndOrderIdIsNull(item.getId())
                        .switchIfEmpty(Mono.just(CartItem.builder()
                                .quantity(0)
                                .itemId(item.getId())
                                .build()))
                        .flatMap(it -> updateCartItemQuantity(it, action))
                        .flatMap(cartItemRepository::save)
                        .flatMap(it -> Mono.zip(
                                Mono.just(item),
                                imageRepository.findByItemId(it.getItemId()),
                                cartItemRepository.countByItemId(it.getItemId()).defaultIfEmpty(0)
                        ))
                )
                .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    public Mono<OrderInfo> purchaseOrder() {
        return cartItemRepository.findAllByOrderIdIsNull()
                .switchIfEmpty(Mono.error(new EntityIsEmptyException("Cart is empty!")))
                .collectList()
                .flatMap(cartItems -> Mono.zip(
                        Mono.just(cartItems),
                        calculateTotalSum(cartItems)
                ))
                .flatMap(tuple -> processOrder(tuple.getT1(), tuple.getT2()));
    }

    @Override
    public Mono<Void> deleteById(Long id, Action action) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Item not found!")))
                .flatMap(it -> cartItemRepository.findByItemIdAndOrderIdIsNull(it.getId()))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Cart item not found!")))
                .flatMap(it -> {
                    if (action == Action.DELETE) return cartItemRepository.deleteByItemId(it.getItemId());
                    return Mono.empty();
                });
    }

    private Mono<OrderInfo> processOrder(List<CartItem> cartItems, BigDecimal totalSum) {
        Order order = Order.builder()
                .totalSum(totalSum)
                .createdAt(LocalDateTime.now())
                .build();

        Mono<List<CartItemInfo>> cartItemInfosMono = Flux.fromIterable(cartItems)
                .flatMap(cartItem ->
                        itemRepository.findById(cartItem.getItemId())
                                .map(item -> CartItemMapper.cartItemToCartItemInfo(cartItem, item))
                )
                .collectList();

        return orderRepository.save(order)
                .doOnNext(newOrder -> cartItems.forEach(cartItem -> cartItem.setOrderId(newOrder.getId())))
                .flatMap(newOrder -> cartItemRepository.saveAll(cartItems).then().thenReturn(newOrder))
                .zipWith(cartItemInfosMono)
                .map(tuple -> OrderMapper.orderToOrderInfo(tuple.getT1(), tuple.getT2()));
    }

    private Mono<BigDecimal> calculateTotalSum(List<CartItem> cartItems) {
        return Flux.fromIterable(cartItems)
                .flatMap(cartItem ->
                        itemRepository.findById(cartItem.getItemId())
                                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Mono<CartItem> updateCartItemQuantity(CartItem cartItem, Action action) {
        return Mono.fromCallable(() -> {
            int newQuantity = cartItem.getQuantity();
            switch (action) {
                case PLUS -> newQuantity++;
                case MINUS -> newQuantity = Math.max(0, newQuantity - 1);
            }
            cartItem.setQuantity(newQuantity);
            return cartItem;
        });
    }
}