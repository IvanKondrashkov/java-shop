package ru.yandex.practicum.service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.BalanceResponse;

public interface BalanceService {
    Mono<BalanceResponse> findByUserId(Long userId);
}