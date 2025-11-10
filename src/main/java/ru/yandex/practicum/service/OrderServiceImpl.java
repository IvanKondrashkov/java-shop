package ru.yandex.practicum.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.dto.OrderInfo;
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
    public OrderInfo findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Order not found!")
        );
        return OrderMapper.orderToOrderInfo(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderInfo> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::orderToOrderInfo)
                .toList();
    }

    @Override
    public OrderInfo buy() {
        return cartItemService.purchaseOrder();
    }
}