package ru.yandex.practicum.repository;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Balance;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

@Repository
public interface BalanceRepository extends R2dbcRepository<Balance, Long> {
    Mono<Balance> findByUserId(Long userId);
}