package ru.yandex.practicum.controller;

import java.util.Set;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.response.OrderInfo;
import ru.yandex.practicum.service.OrderService;
import static org.mockito.Mockito.*;

@WebFluxTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private OrderService orderService;
    private OrderInfo orderInfo;

    @BeforeEach
    void setUp() {
        orderInfo = OrderInfo.builder()
                .id(1L)
                .totalSum(BigDecimal.valueOf(14999.00))
                .items(Set.of())
                .build();
    }

    @AfterEach
    void tearDown() {
        orderInfo = null;
    }

    @Test
    void findById() {
        when(orderService.findById(any())).thenReturn(Mono.just(orderInfo));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/orders/1")
                        .queryParam("newOrder", "false").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("14999");
                    assert html.contains("items");
                    assert html.contains("order");
                });

        verify(orderService, times(1)).findById(any());
    }

    @Test
    void findAll() {
        when(orderService.findAll()).thenReturn(Flux.just(orderInfo));

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("14999");
                    assert html.contains("items");
                    assert html.contains("order");
                    assert html.contains("orders");
                });

        verify(orderService, times(1)).findAll();
    }

    @Test
    void buy() {
        when(orderService.buy()).thenReturn(Mono.just(orderInfo));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/buy")
                        .queryParam("newOrder", "false").build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/orders/1?newOrder=true");

        verify(orderService, times(1)).buy();
    }
}