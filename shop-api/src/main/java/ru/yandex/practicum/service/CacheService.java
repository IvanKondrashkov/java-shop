package ru.yandex.practicum.service;

import java.util.List;
import java.time.Duration;
import reactor.core.publisher.Mono;

public interface CacheService {
    <T> Mono<T> save(String prefix, String id, T entity, Duration duration);
    <T> Mono<T> get(String prefix, String id, Class<T> clazz);
    <T> Mono<Boolean> saveList(String key, List<T> entities, Duration duration);
    <T> Mono<List<T>> getList(String key, Class<T> clazz);
}