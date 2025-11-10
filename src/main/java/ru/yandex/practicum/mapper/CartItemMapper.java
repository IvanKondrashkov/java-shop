package ru.yandex.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.dto.CartItemInfo;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CartItemMapper {
    public static CartItemInfo cartItemToCartItemInfo(CartItem cartItem) {
        return CartItemInfo.builder()
                .id(cartItem.getId())
                .title(cartItem.getItem().getTitle())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getItem().getPrice())
                .build();
    }
}