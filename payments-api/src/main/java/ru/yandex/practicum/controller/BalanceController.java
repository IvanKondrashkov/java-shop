package ru.yandex.practicum.controller;

import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.server.api.BalanceApi;
import ru.yandex.practicum.service.BalanceService;
import org.springframework.web.server.ServerWebExchange;
import ru.yandex.practicum.server.model.BalanceResponse;

@RestController
@RequiredArgsConstructor
public class BalanceController implements BalanceApi {
    private final BalanceService balanceService;

    @Override
    public Mono<ResponseEntity<BalanceResponse>> balanceUserIdGet(Long userId, ServerWebExchange exchange) {
        return balanceService.findByUserId(userId)
                .map(ResponseEntity::ok);
    }
}