package ru.yandex.practicum.controller;

import java.util.UUID;
import java.nio.file.Files;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.dto.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.service.ItemService;
import ru.yandex.practicum.service.AdminService;
import ru.yandex.practicum.service.CartItemService;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
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
    void redirectToItems() throws Exception {
        mockMvc.perform(
                get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    void findById() throws Exception {
        when(itemService.findById(1L)).thenReturn(itemInfo);

        mockMvc.perform(
                get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));

        verify(itemService, times(1)).findById(1L);
    }

    @Test
    void findAll() throws Exception {
        when(itemService.findAll(any())).thenReturn(new PageImpl<>(Collections.singletonList(itemInfo)));

        mockMvc.perform(
                get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"));

        verify(itemService, times(1)).findAll(any());
    }

    @Test
    void findAllBySearch() throws Exception {
        when(itemService.findAllBySearch(anyString(), any())).thenReturn(new PageImpl<>(Collections.singletonList(itemInfo)));

        mockMvc.perform(
                get("/items")
                        .param("search", "SSD"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("search"));

        verify(itemService, times(1)).findAllBySearch(anyString(), any());
    }

    @Test
    void purchaseItemById() throws Exception {
        when(cartItemService.purchaseItem(any(), any())).thenReturn(itemInfo);

        mockMvc.perform(
                        post("/items/1")
                                .param("action", Action.PLUS.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));

        verify(cartItemService, times(1)).purchaseItem(any(), any());
    }

    @Test
    void purchaseItem() throws Exception {
        when(cartItemService.purchaseItem(any(), any())).thenReturn(itemInfo);

        mockMvc.perform(
                post("/items")
                        .param("id", "1")
                        .param("action", Action.PLUS.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        String.format("/items?search=%s&sort=%s&order=%s&pageNumber=%d&pageSize=%d", "", Sort.NO, Order.DESC, 1, 5))
                );

        verify(cartItemService, times(1)).purchaseItem(any(), any());
    }

    @Test
    void importCsvFile() throws Exception {
        ClassPathResource resource = new ClassPathResource("static/items.csv");
        byte[] bytes = Files.readAllBytes(resource.getFile().toPath());

        MockMultipartFile file = new MockMultipartFile("file", bytes);

        mockMvc.perform(
                        multipart("/items/upload")
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        String.format("/items?search=%s&sort=%s&order=%s&pageNumber=%d&pageSize=%d", "", Sort.NO, Order.DESC, 1, 5))
                );

        verify(adminService, times(1)).importCsvFile(file);
    }
}