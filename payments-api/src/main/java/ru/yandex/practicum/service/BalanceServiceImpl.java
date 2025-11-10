package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.mapper.BalanceMapper;
import ru.yandex.practicum.repository.BalanceRepository;
import ru.yandex.practicum.repository.UserRepository;
import ru.yandex.practicum.server.model.BalanceResponse;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final UserRepository userRepository;
    private final BalanceRepository balanceRepository;

    @Transactional(readOnly = true)
    public Mono<BalanceResponse> findByUserId(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found!")))
                .then(balanceRepository.findByUserId(userId))
                .map(BalanceMapper::balanceToBalanceResponse);
    }
}