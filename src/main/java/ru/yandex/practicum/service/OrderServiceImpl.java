package ru.yandex.practicum.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.response.OrderInfo;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.exception.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final CartItemService cartItemService;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderInfo> findById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Order not found!")))
                .flatMap(it ->
                        cartItemService.findAllByOrderId(it.getId())
                                .collectList()
                                .map(cartItems -> OrderMapper.orderToOrderInfo(it, cartItems))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderInfo> findAll() {
        return orderRepository.findAll()
                .flatMap(it ->
                        cartItemService.findAllByOrderId(it.getId())
                                .collectList()
                                .map(cartItems -> OrderMapper.orderToOrderInfo(it, cartItems))
                );
    }

    @Override
    public Mono<OrderInfo> buy() {
        return cartItemService.purchaseOrder();
    }
}