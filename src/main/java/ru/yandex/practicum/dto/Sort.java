package ru.yandex.practicum.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sort {
    ALPHA("title"),
    PRICE("price"),
    NO("no");

    private final String value;

    public static Sort fromValue(String value) {
        for (Sort sort : Sort.values()) {
            if (sort.value.equalsIgnoreCase(value)) {
                return sort;
            }
        }
        return NO;
    }
}