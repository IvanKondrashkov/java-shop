package ru.yandex.practicum.controller;

import java.util.UUID;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.dto.response.ImageInfo;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.service.ItemService;
import ru.yandex.practicum.service.AdminService;
import ru.yandex.practicum.service.CartItemService;
import static org.mockito.Mockito.*;

@WebFluxTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private CartItemService cartItemService;
    @MockitoBean
    private AdminService adminService;
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
    void redirectToItems() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/items");
    }

    @Test
    void findById() {
        when(itemService.findById(any())).thenReturn(Mono.just(itemInfo));

        webTestClient.get()
                .uri("/items/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Samsung T7");
                    assert html.contains("14999");
                    assert html.contains("1ТБ");
                    assert html.contains("1050 МБ/с");
                    assert html.contains("item");
                });

        verify(itemService, times(1)).findById(any());
    }

    @Test
    void findAll() {
        when(itemService.findAll(any(), any(), any())).thenReturn(Flux.just(itemInfo));
        when(itemService.count()).thenReturn(Mono.just(1L));

        webTestClient.get()
                .uri("/items")
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

        verify(itemService, times(1)).findAll(any(), any(), any());
        verify(itemService, times(1)).count();
    }

    @Test
    void findAllBySearch() {
        when(itemService.findAllBySearch(anyString(), any(), any(), any())).thenReturn(Flux.just(itemInfo));
        when(itemService.countBySearch(anyString())).thenReturn(Mono.just(1L));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("search", "SSD")
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

        verify(itemService, times(1)).findAllBySearch(anyString(), any(), any(), any());
        verify(itemService, times(1)).countBySearch(anyString());
    }

    @Test
    void purchaseItemById() {
        when(cartItemService.purchaseItem(any(), any())).thenReturn(Mono.just(itemInfo));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items/1")
                        .queryParam("action", Action.PLUS.name())
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
                });

        verify(cartItemService, times(1)).purchaseItem(any(), any());
    }

    @Test
    void purchaseItem() {
        when(cartItemService.purchaseItem(any(), any())).thenReturn(Mono.just(itemInfo));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("id", "1")
                        .queryParam("action", Action.PLUS.name())
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location(String.format("/items?search=%s&sort=%s&order=%s&pageNumber=%d&pageSize=%d", "", SortType.NO, OrderType.DESC, 1, 5));

        verify(cartItemService, times(1)).purchaseItem(any(), any());
    }

    @Test
    void importCsvFile() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("static/items.csv"))
                .filename("items.csv");

        when(adminService.importCsvFile(any())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/items/upload")
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location(String.format("/items?search=%s&sort=%s&order=%s&pageNumber=%d&pageSize=%d", "", SortType.NO, OrderType.DESC, 1, 5));

        verify(adminService, times(1)).importCsvFile(any());
    }
}