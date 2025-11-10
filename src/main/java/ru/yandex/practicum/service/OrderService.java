package ru.yandex.practicum.service;

import java.util.List;
import ru.yandex.practicum.dto.OrderInfo;

public interface OrderService {
    OrderInfo findById(Long id);
    List<OrderInfo> findAll();
    OrderInfo buy();
}