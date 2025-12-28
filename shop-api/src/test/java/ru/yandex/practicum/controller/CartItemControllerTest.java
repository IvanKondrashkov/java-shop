package ru.yandex.practicum.controller;

import java.util.UUID;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.response.ImageInfo;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.service.CartItemService;
import ru.yandex.practicum.service.UserService;
import static org.mockito.Mockito.*;

public class CartItemControllerTest extends BaseControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private PaymentClient paymentClient;
    @MockitoBean
    private CartItemService cartItemService;
    @MockitoBean
    private UserService userService;
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
    @WithMockUser(username = "Djon")
    void findAll() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cartItemService.findAll()).thenReturn(Flux.just(itemInfo));
        when(paymentClient.getBalance(1L)).thenReturn(Mono.empty());

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

        verify(userService, times(1)).getCurrentUserId();
        verify(cartItemService, times(1)).findAll();
        verify(paymentClient, times(1)).getBalance(1L);
    }

    @Test
    @WithMockUser(username = "Djon")
    void purchaseItem() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cartItemService.purchaseItem(any(), any())).thenReturn(Mono.just(itemInfo));
        when(cartItemService.findAll()).thenReturn(Flux.just(itemInfo));
        when(paymentClient.getBalance(1L)).thenReturn(Mono.empty());

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

        verify(userService, times(1)).getCurrentUserId();
        verify(cartItemService, times(1)).purchaseItem(any(), any());
        verify(cartItemService, times(1)).findAll();
        verify(paymentClient, times(1)).getBalance(1L);
    }
}