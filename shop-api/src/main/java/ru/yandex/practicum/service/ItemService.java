package ru.yandex.practicum.service;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.dto.response.ItemInfo;

public interface ItemService {
    Mono<ItemInfo> findById(Long id);
    Flux<ItemInfo> findAll(Integer limit, Integer offset, String sort);
    Flux<ItemInfo> findAllBySearch(String search, Integer limit, Integer offset, String sort);
    Mono<Long> count();
    Mono<Long> countBySearch(String search);
}