package ru.yandex.practicum.client;

import java.time.Duration;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.client.api.BalanceApi;
import ru.yandex.practicum.client.api.PaymentsApi;
import ru.yandex.practicum.client.model.BalanceResponse;
import ru.yandex.practicum.client.model.PaymentRequest;
import ru.yandex.practicum.client.model.PaymentResponse;
import io.netty.handler.timeout.TimeoutException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.PaymentProcessException;
import ru.yandex.practicum.exception.PaymentServiceUnavailableException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static org.springframework.http.HttpStatus.*;


@Component
@RequiredArgsConstructor
public class PaymentClient {
    private final BalanceApi balanceApi;
    private final PaymentsApi paymentsApi;

    public Mono<BalanceResponse> getBalance(Long userId) {
        return balanceApi.balanceUserIdGet(userId)
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(this::processError);
    }

    public Mono<PaymentResponse> processPayment(PaymentRequest paymentRequest) {
        return paymentsApi.paymentsProcessPost(paymentRequest)
                .timeout(Duration.ofSeconds(30))
                .onErrorResume(this::processError);
    }


    private <T> Mono<T> processError(Throwable e) {
        return switch (e) {
            case WebClientResponseException ex -> Mono.error(mapClientError(ex));
            case TimeoutException ex -> Mono.error(new PaymentServiceUnavailableException("Payment service unavailable!", ex));
            default -> Mono.error(new PaymentProcessException("Unexpected payment error", e));
        };
    }

    private RuntimeException mapClientError(WebClientResponseException ex) {
        return switch (ex.getStatusCode()) {
            case NOT_FOUND -> new EntityNotFoundException("Entity not found!");
            case BAD_REQUEST -> new PaymentProcessException("Invalid payment request!");
            case SERVICE_UNAVAILABLE -> new PaymentServiceUnavailableException("Payment service unavailable!", ex);
            default -> new PaymentProcessException("Payment error: " + ex.getStatusCode().value());
        };
    }
}