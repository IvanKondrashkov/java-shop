package ru.yandex.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.dto.response.CartItemInfo;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CartItemMapper {
    public static CartItemInfo cartItemToCartItemInfo(CartItem cartItem, Item item) {
        return CartItemInfo.builder()
                .id(cartItem.getId())
                .title(item.getTitle())
                .quantity(cartItem.getQuantity())
                .price(item.getPrice())
                .build();
    }
}