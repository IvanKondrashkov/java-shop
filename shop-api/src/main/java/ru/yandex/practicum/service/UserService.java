package ru.yandex.practicum.service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.User;

public interface UserService {
    Mono<User> findById(Long id);
    Mono<Long> getCurrentUserId();
    Mono<Boolean> existsByUsername(String username);
    Mono<User> register(User user);
}