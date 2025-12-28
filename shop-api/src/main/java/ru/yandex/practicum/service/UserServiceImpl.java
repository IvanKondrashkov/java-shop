package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.EntityConflictException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found!")));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .flatMap(userRepository::findByUsername)
                .map(User::getId);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existsByUsername(String username) {
        return userRepository.findByUsername(username)
                .hasElement();
    }

    @Override
    public Mono<User> register(User user) {
        return existsByUsername(user.getUsername())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new EntityConflictException("User already exists!"));
                    }
                    return userRepository.save(user)
                            .doOnSuccess(newUser -> log.info("User registered successfully: {}", newUser.getUsername()));
                });
    }
}