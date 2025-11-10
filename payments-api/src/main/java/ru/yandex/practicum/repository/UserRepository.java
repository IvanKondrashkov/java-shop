package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
}