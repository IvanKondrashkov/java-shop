package ru.yandex.practicum.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class CacheUtils {
    public static String buildKey(String key, String subKey) {
        return String.join(":", key, subKey);
    }
}
