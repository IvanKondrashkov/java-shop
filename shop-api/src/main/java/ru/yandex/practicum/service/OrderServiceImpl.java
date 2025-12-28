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
    private final OrderRepository orderRepository;
    private final CartItemService cartItemService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderInfo> findById(Long id) {
        return userService.getCurrentUserId()
                .flatMap(userId -> orderRepository.findByIdAndUserId(id, userId))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Order not found!")))
                .flatMap(order -> cartItemService.findAllByOrderId(order.getId())
                        .collectList()
                        .map(cartItems -> OrderMapper.orderToOrderInfo(order, cartItems))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderInfo> findAll() {
        return userService.getCurrentUserId()
                .flatMapMany(orderRepository::findAllByUserId)
                .flatMap(order -> cartItemService.findAllByOrderId(order.getId())
                        .collectList()
                        .map(cartItems -> OrderMapper.orderToOrderInfo(order, cartItems))
                );
    }

    @Override
    public Mono<OrderInfo> buy() {
        return userService.getCurrentUserId()
                .flatMap(cartItemService::purchaseOrder);
    }
}