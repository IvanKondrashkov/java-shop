package ru.yandex.practicum.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Action {
    PLUS("plus"),
    MINUS("minus"),
    DELETE("delete"),
    NO("no");

    private final String value;

    public static Action fromValue(String value) {
        for (Action order : Action.values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
        return NO;
    }
}