package ru.yandex.practicum.service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.server.model.PaymentRequest;
import ru.yandex.practicum.server.model.PaymentResponse;

public interface PaymentService {
    Mono<PaymentResponse> processPayment(PaymentRequest paymentRequest);
}