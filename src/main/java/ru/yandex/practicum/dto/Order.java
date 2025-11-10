package ru.yandex.practicum.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Order {
    ASC("asc"),
    DESC("desc");

    private final String value;

    public static Order fromValue(String value) {
        for (Order order : Order.values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
        return DESC;
    }
}