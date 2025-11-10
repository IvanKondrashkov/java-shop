package ru.yandex.practicum.controller;

import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.server.api.PaymentsApi;
import ru.yandex.practicum.service.PaymentService;
import org.springframework.web.server.ServerWebExchange;
import ru.yandex.practicum.server.model.PaymentRequest;
import ru.yandex.practicum.server.model.PaymentResponse;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentsApi {
    private final PaymentService paymentService;

    @Override
    public Mono<ResponseEntity<PaymentResponse>> paymentsProcessPost(Mono<PaymentRequest> paymentRequest, ServerWebExchange exchange) {
        return paymentRequest
                .flatMap(paymentService::processPayment)
                .map(ResponseEntity::ok);
    }
}