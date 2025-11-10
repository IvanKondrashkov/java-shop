package ru.yandex.practicum.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    ID("id"),
    ALPHA("title"),
    PRICE("price"),
    NO("no");

    private final String value;

    public static SortType fromValue(String value) {
        for (SortType sortType : SortType.values()) {
            if (sortType.value.equalsIgnoreCase(value)) {
                return sortType;
            }
        }
        return NO;
    }
}