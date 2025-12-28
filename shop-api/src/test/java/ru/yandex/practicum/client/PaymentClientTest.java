package ru.yandex.practicum.client;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import reactor.test.StepVerifier;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.client.api.BalanceApi;
import ru.yandex.practicum.client.api.PaymentsApi;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.client.model.BalanceResponse;
import ru.yandex.practicum.client.model.PaymentResponse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentClientTest {
    @Mock
    private BalanceApi balanceApi;
    @Mock
    private PaymentsApi paymentsApi;
    @InjectMocks
    private PaymentClient paymentClient;

    @Test
    void getBalance() {
        BalanceResponse balance = new BalanceResponse();
        balance.setUserId(1L);
        balance.setBalance(new BigDecimal("0.0"));

        when(balanceApi.balanceUserIdGet(1L)).thenReturn(Mono.just(balance));

        StepVerifier.create(paymentClient.getBalance(1L))
                .expectNextMatches(newBalance ->
                        newBalance != null &&
                        newBalance.getUserId().equals(1L) &&
                        newBalance.getBalance().compareTo(new BigDecimal("0.0")) == 0 &&
                        newBalance.getCurrency().equals("RUB")
                )
                .verifyComplete();

        verify(balanceApi, times(1)).balanceUserIdGet(1L);
    }

    @Test
    void processPayment() {
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentId(1L);
        paymentResponse.setUserId(1L);
        paymentResponse.setAmount(new BigDecimal("10.0"));
        paymentResponse.setCurrency("RUB");
        paymentResponse.setStatus(PaymentResponse.StatusEnum.SUCCESS);
        paymentResponse.setTimestamp(OffsetDateTime.now());

        when(paymentsApi.paymentsProcessPost(any())).thenReturn(Mono.just(paymentResponse));

        StepVerifier.create(paymentClient.processPayment(any()))
                .expectNextMatches(newPayment ->
                        newPayment != null &&
                        newPayment.getPaymentId().equals(1L) &&
                        newPayment.getUserId().equals(1L) &&
                        newPayment.getAmount().compareTo(new BigDecimal("10.0")) == 0 &&
                        newPayment.getCurrency().equals("RUB") &&
                        newPayment.getStatus().equals(PaymentResponse.StatusEnum.SUCCESS) &&
                        newPayment.getTimestamp() != null
                )
                .verifyComplete();

        verify(paymentsApi, times(1)).paymentsProcessPost(any());
    }
}