package ru.yandex.practicum.controller;

import java.util.UUID;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.response.ImageInfo;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.service.CartItemService;
import static org.mockito.Mockito.*;

@WebFluxTest(CartItemController.class)
public class CartItemControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private CartItemService cartItemService;
    private ImageInfo imageInfo;
    private ItemInfo itemInfo;

    @BeforeEach
    void setUp() {
        String fileName = UUID.randomUUID().toString();
        imageInfo = ImageInfo.builder()
                .fileName(fileName)
                .imageUrl("https://storage.yandexcloud.net/java-shop-image-storage/" + fileName)
                .build();
        itemInfo = ItemInfo.builder()
                .id(1L)
                .title("Внешний SSD Samsung T7")
                .description("Portable SSD 1ТБ со скоростью передачи до 1050 МБ/с")
                .price(BigDecimal.valueOf(14999.00))
                .count(1)
                .image(imageInfo)
                .build();
    }

    @AfterEach
    void tearDown() {
        imageInfo = null;
        itemInfo = null;
    }

    @Test
    void findAll() {
        when(cartItemService.findAll()).thenReturn(Flux.just(itemInfo));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Samsung T7");
                    assert html.contains("14999");
                    assert html.contains("1ТБ");
                    assert html.contains("1050 МБ/с");
                    assert html.contains("item");
                    assert html.contains("items");
                });

        verify(cartItemService, times(1)).findAll();
    }

    @Test
    void purchaseItem() {
        when(cartItemService.purchaseItem(any(), any())).thenReturn(Mono.just(itemInfo));
        when(cartItemService.findAll()).thenReturn(Flux.just(itemInfo));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam("id", itemInfo.getId().toString())
                        .queryParam("action", Action.PLUS)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Samsung T7");
                    assert html.contains("14999");
                    assert html.contains("1ТБ");
                    assert html.contains("1050 МБ/с");
                    assert html.contains("item");
                    assert html.contains("items");
                });

        verify(cartItemService, times(1)).purchaseItem(any(), any());
        verify(cartItemService, times(1)).findAll();
    }
}