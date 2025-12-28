package ru.yandex.practicum.service;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.model.PaymentRequest;
import ru.yandex.practicum.client.model.PaymentResponse;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;
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
import ru.yandex.practicum.exception.PaymentProcessException;

@Service
@Transactional
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final CacheService cacheService;
    private final PaymentClient paymentClient;

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemInfo> findAll() {
        return cartItemRepository.findAllByOrderIdIsNull()
                .flatMap(it -> Mono.zip(
                        processItem(it.getItemId()),
                        processImage(it.getItemId()),
                        cartItemRepository.countByItemId(it.getItemId()).defaultIfEmpty(0))
                )
                .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CartItemInfo> findAllByOrderId(Long id) {
        return cartItemRepository.findAllByOrderId(id)
                .flatMap(it -> Mono.zip(Mono.just(it), processItem(it.getItemId()))
                .map(tuple -> CartItemMapper.cartItemToCartItemInfo(tuple.getT1(), tuple.getT2())));
    }

    @Override
    public Mono<ItemInfo> purchaseItem(Long id, Action action) {
        return processItem(id)
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
                                processImage(item.getId()),
                                cartItemRepository.countByItemId(it.getItemId()).defaultIfEmpty(0)
                        ))
                )
                .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    public Mono<OrderInfo> purchaseOrder(Long userId) {
        return cartItemRepository.findAllByOrderIdIsNull()
                .switchIfEmpty(Mono.error(new EntityIsEmptyException("Cart is empty!")))
                .collectList()
                .flatMap(cartItems -> Mono.zip(Mono.just(cartItems), calculateTotalSum(cartItems)))
                .flatMap(tuple -> processOrder(userId, tuple.getT1(), tuple.getT2()));
    }

    @Override
    public Mono<Void> deleteById(Long id, Action action) {
        return processItem(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Item not found!")))
                .flatMap(it -> cartItemRepository.findByItemIdAndOrderIdIsNull(it.getId()))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Cart item not found!")))
                .flatMap(it -> {
                    if (action == Action.DELETE) return cartItemRepository.deleteByItemId(it.getItemId());
                    return Mono.empty();
                });
    }

    private Mono<OrderInfo> processOrder(Long userId, List<CartItem> cartItems, BigDecimal totalSum) {
        Order order = Order.builder()
                .totalSum(totalSum)
                .createdAt(LocalDateTime.now())
                .build();

        Mono<List<CartItemInfo>> cartItemInfosMono = Flux.fromIterable(cartItems)
                .flatMap(cartItem ->
                        processItem(cartItem.getItemId())
                                .map(item -> CartItemMapper.cartItemToCartItemInfo(cartItem, item))
                )
                .collectList();

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(totalSum);
        paymentRequest.setUserId(userId);

        return orderRepository.save(order)
                .doOnNext(newOrder -> cartItems.forEach(cartItem -> cartItem.setOrderId(newOrder.getId())))
                .flatMap(newOrder -> cartItemRepository.saveAll(cartItems).then().thenReturn(newOrder))
                .flatMap(newOrder -> processPayment(newOrder, cartItemInfosMono, paymentRequest));
    }

    private Mono<OrderInfo> processPayment(Order order, Mono<List<CartItemInfo>> cartItemInfosMono, PaymentRequest paymentRequest) {
        return Mono.zip(
                        cartItemInfosMono,
                        paymentClient.processPayment(paymentRequest)
                )
                .flatMap(tuple -> {
                    PaymentResponse paymentResponse = tuple.getT2();
                    if (paymentResponse.getStatus() == PaymentResponse.StatusEnum.FAILED) {
                        return orderRepository.deleteById(order.getId())
                                .then(Mono.error(new PaymentProcessException("Order creation failed!")));
                    }

                    order.setStatus(paymentResponse.getStatus().getValue());
                    return orderRepository.save(order)
                            .map(newOrder -> OrderMapper.orderToOrderInfo(newOrder, tuple.getT1()));
                });
    }

    private Mono<Item> processItem(Long id) {
        return cacheService.getItem(id.toString())
                .switchIfEmpty(itemRepository.findById(id))
                .flatMap(item -> cacheService.addItem(item).thenReturn(item));
    }

    private Mono<Image> processImage(Long itemId) {
        return cacheService.getImage(itemId.toString())
                .switchIfEmpty(imageRepository.findByItemId(itemId))
                .flatMap(image -> cacheService.addImage(itemId.toString(), image).thenReturn(image));
    }

    private Mono<BigDecimal> calculateTotalSum(List<CartItem> cartItems) {
        return Flux.fromIterable(cartItems)
                .flatMap(cartItem ->
                        processItem(cartItem.getItemId())
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