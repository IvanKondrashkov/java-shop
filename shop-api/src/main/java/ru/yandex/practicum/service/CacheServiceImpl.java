package ru.yandex.practicum.service;

import java.util.List;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.utils.CacheUtils;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Boolean> addImage(Image image) {
        return reactiveRedisTemplate.opsForValue()
                .set(CacheUtils.buildKey("image", image.getFileName()), image, Duration.ofMinutes(3));
    }

    @Override
    public Mono<Boolean> addImage(String itemId, Image image) {
        return reactiveRedisTemplate.opsForValue()
                .set(CacheUtils.buildKey("image", itemId), image, Duration.ofMinutes(10));
    }

    @Override
    public Mono<Image> getImage(String key) {
        return reactiveRedisTemplate.opsForValue()
                .get(CacheUtils.buildKey("image", key))
                .map(obj -> objectMapper.convertValue(obj, Image.class));
    }

    @Override
    public Mono<Boolean> addItem(Item item) {
        return reactiveRedisTemplate.opsForValue()
                .set(CacheUtils.buildKey("item", item.getId().toString()), item, Duration.ofMinutes(10));
    }

    @Override
    public Mono<Item> getItem(String id) {
        return reactiveRedisTemplate.opsForValue()
                .get(CacheUtils.buildKey("item", id))
                .map(obj -> objectMapper.convertValue(obj, Item.class));
    }

    @Override
    public Mono<Boolean> addItems(String key, List<Item> items) {
        return !items.isEmpty() ? reactiveRedisTemplate.opsForList()
                .rightPushAll(key, items.toArray())
                .then(reactiveRedisTemplate.expire(key, Duration.ofMinutes(10))) : Mono.empty();
    }

    @Override
    public Mono<List<Item>> getItems(String key) {
        return reactiveRedisTemplate.opsForList()
                .range(key, 0, -1)
                .map(obj -> objectMapper.convertValue(obj, Item.class))
                .collectList();
    }

    @Override
    public Mono<Boolean> addItemsSearch(String key, List<Item> items) {
        return !items.isEmpty() ? reactiveRedisTemplate.opsForList()
                .rightPushAll(key, items.toArray())
                .then(reactiveRedisTemplate.expire(key, Duration.ofMinutes(5))) : Mono.empty();
    }

    @Override
    public Mono<List<Item>> getItemsSearch(String key) {
        return reactiveRedisTemplate.opsForList()
                .range(key, 0, -1)
                .map(obj -> objectMapper.convertValue(obj, Item.class))
                .collectList();
    }
}