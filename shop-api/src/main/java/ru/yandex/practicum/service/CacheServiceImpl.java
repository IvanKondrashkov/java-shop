package ru.yandex.practicum.service;

import java.util.List;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.utils.CacheUtils;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> Mono<T> save(String prefix, String id, T entity, Duration duration) {
        String key = CacheUtils.buildKey(prefix, id);
        return reactiveRedisTemplate.opsForValue()
                .set(key, entity, duration)
                .thenReturn(entity);
    }

    @Override
    public <T> Mono<T> get(String prefix, String id, Class<T> clazz) {
        String key = CacheUtils.buildKey(prefix, id);
        return reactiveRedisTemplate.opsForValue()
                .get(key)
                .map(obj -> objectMapper.convertValue(obj, clazz));
    }

    @Override
    public <T> Mono<Boolean> saveList(String key, List<T> entities, Duration duration) {
        return !entities.isEmpty() ? reactiveRedisTemplate.opsForList()
                .rightPushAll(key, entities.toArray())
                .then(reactiveRedisTemplate.expire(key, duration)) : Mono.empty();
    }

    @Override
    public <T> Mono<List<T>> getList(String key, Class<T> clazz) {
        return reactiveRedisTemplate.opsForList()
                .range(key, 0, -1)
                .map(obj -> objectMapper.convertValue(obj, clazz))
                .collectList();
    }
}