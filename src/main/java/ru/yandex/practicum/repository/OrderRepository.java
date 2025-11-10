package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.Order;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {
}