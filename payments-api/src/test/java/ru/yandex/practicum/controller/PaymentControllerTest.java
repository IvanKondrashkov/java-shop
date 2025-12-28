package ru.yandex.practicum.controller;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.service.PaymentService;
import ru.yandex.practicum.server.model.PaymentRequest;
import ru.yandex.practicum.server.model.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.*;

public class PaymentControllerTest extends BaseControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private PaymentService paymentService;
    private PaymentRequest paymentRequest;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(1L);
        paymentRequest.setAmount(BigDecimal.valueOf(14999.00));

        paymentResponse = new PaymentResponse();
        paymentResponse.setUserId(1L);
        paymentResponse.setAmount(BigDecimal.valueOf(14999.00));
        paymentResponse.setStatus(PaymentResponse.StatusEnum.SUCCESS);
    }

    @AfterEach
    void tearDown() {
        paymentRequest = null;
        paymentResponse = null;
    }

    @Test
    @WithMockUser(username = "Djon")
    void balanceUserIdGet() {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(Mono.just(paymentResponse));

        webTestClient.post()
                .uri("/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(paymentRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PaymentResponse.class)
                .isEqualTo(paymentResponse);

        verify(paymentService, times(1)).processPayment(any(PaymentRequest.class));
    }
}