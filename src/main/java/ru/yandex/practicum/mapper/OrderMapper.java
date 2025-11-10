package ru.yandex.practicum.mapper;

import java.util.List;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.dto.response.OrderInfo;
import ru.yandex.practicum.dto.response.CartItemInfo;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class OrderMapper {
    public static OrderInfo orderToOrderInfo(Order order, List<CartItemInfo> cartItems) {
        return OrderInfo.builder()
                .id(order.getId())
                .totalSum(order.getTotalSum())
                .items(new HashSet<>(cartItems))
                .build();
    }
}