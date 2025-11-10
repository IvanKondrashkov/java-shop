package ru.yandex.practicum.mapper;

import lombok.AccessLevel;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import java.util.stream.Collectors;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.dto.OrderInfo;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class OrderMapper {
    public static OrderInfo orderToOrderInfo(Order order) {
        return OrderInfo.builder()
                .id(order.getId())
                .totalSum(order.getTotalSum())
                .items(order.getItems().stream()
                        .map(CartItemMapper::cartItemToCartItemInfo)
                        .collect(Collectors.toSet())
                )
                .build();
    }
}