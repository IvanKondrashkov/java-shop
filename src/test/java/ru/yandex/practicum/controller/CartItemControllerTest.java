package ru.yandex.practicum.controller;

import java.util.UUID;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.dto.*;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.service.CartItemService;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(CartItemController.class)
public class CartItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
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
    void findAll() throws Exception {
        when(cartItemService.findAll()).thenReturn(Collections.singletonList(itemInfo));

        mockMvc.perform(
                        get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"));

        verify(cartItemService, times(1)).findAll();
    }

    @Test
    void purchaseItem() throws Exception {
        when(cartItemService.purchaseItem(any(), any())).thenReturn(itemInfo);

        mockMvc.perform(
                        post("/cart/items")
                                .param("id", itemInfo.getId().toString())
                                .param("action", Action.PLUS.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"));

        verify(cartItemService, times(1)).purchaseItem(any(), any());
    }
}