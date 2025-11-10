package ru.yandex.practicum.service;

import java.util.List;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.dto.ItemInfo;
import ru.yandex.practicum.dto.OrderInfo;

public interface CartItemService {
    List<ItemInfo> findAll();
    ItemInfo purchaseItem(Long id, Action action);
    OrderInfo purchaseOrder();
    void deleteById(Long id, Action action);
}