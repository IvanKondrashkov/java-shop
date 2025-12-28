package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.Role;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.dto.request.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.yandex.practicum.exception.EntityConflictException;

@Slf4j
@Controller
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public Mono<Rendering> register() {
        return Mono.just(Rendering.view("register").build());
    }

    @PostMapping("/register")
    public Mono<Rendering> register(@ModelAttribute RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return Mono.just(Rendering.view("register")
                    .modelAttribute("error", "Passwords don't match!")
                    .build());
        }

        if (registerRequest.getPassword().length() < 6) {
            return Mono.just(Rendering.view("register")
                    .modelAttribute("error", "The password must contain at least 6 characters!")
                    .build());
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER.name())
                .enabled(true)
                .build();

        return userService.register(user)
                .thenReturn(Rendering.redirectTo("/login").build())
                .onErrorResume(EntityConflictException.class, ex -> Mono.just(
                        Rendering.view("register")
                                .modelAttribute("error", ex.getMessage())
                                .build()
                ));
    }
}