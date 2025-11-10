package ru.yandex.practicum.service;

import java.util.List;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;

public interface CacheService {
    Mono<Boolean> addImage(Image image);
    Mono<Boolean> addImage(String itemId, Image image);
    Mono<Image> getImage(String key);
    Mono<Boolean> addItem(Item item);
    Mono<Item> getItem(String id);
    Mono<Boolean> addItems(String key, List<Item> items);
    Mono<List<Item>> getItems(String key);
    Mono<Boolean> addItemsSearch(String key, List<Item> items);
    Mono<List<Item>> getItemsSearch(String key);
}