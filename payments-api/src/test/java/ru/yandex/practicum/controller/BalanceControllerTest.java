package ru.yandex.practicum.controller;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import ru.yandex.practicum.server.model.BalanceResponse;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.*;

@WebFluxTest(BalanceController.class)
public class BalanceControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private BalanceService balanceService;
    private BalanceResponse balanceResponse;

    @BeforeEach
    void setUp() {
        balanceResponse = new BalanceResponse();
        balanceResponse.setUserId(1L);
        balanceResponse.setBalance(BigDecimal.valueOf(14999.00));
    }

    @AfterEach
    void tearDown() {
        balanceResponse = null;
    }

    @Test
    void balanceUserIdGet() {
        when(balanceService.findByUserId(anyLong())).thenReturn(Mono.just(balanceResponse));

        webTestClient.get()
                .uri("/balance/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo(1L)
                .jsonPath("$.balance").isEqualTo(BigDecimal.valueOf(14999.00))
                .jsonPath("$.currency").isEqualTo("RUB");

        verify(balanceService, times(1)).findByUserId(1L);
    }
}