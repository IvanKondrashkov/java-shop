package ru.yandex.practicum.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderType {
    ASC("asc"),
    DESC("desc");

    private final String value;

    public static OrderType fromValue(String value) {
        for (OrderType orderType : OrderType.values()) {
            if (orderType.value.equalsIgnoreCase(value)) {
                return orderType;
            }
        }
        return DESC;
    }
}