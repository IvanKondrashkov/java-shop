package ru.yandex.practicum.controller;

import java.util.Set;
import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.service.OrderService;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private OrderService orderService;
    private OrderInfo orderInfo;

    @BeforeEach
    void setUp() {
        orderInfo = OrderInfo.builder()
                .id(1L)
                .totalSum(BigDecimal.ZERO)
                .items(Set.of())
                .build();
    }

    @AfterEach
    void tearDown() {
        orderInfo = null;
    }

    @Test
    void findById() throws Exception {
        when(orderService.findById(1L)).thenReturn(orderInfo);

        mockMvc.perform(
                        get("/orders/1")
                                .param("newOrder", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attributeExists("newOrder"));

        verify(orderService, times(1)).findById(1L);
    }

    @Test
    void findAll() throws Exception {
        when(orderService.findAll()).thenReturn(Collections.singletonList(orderInfo));

        mockMvc.perform(
                        get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));

        verify(orderService, times(1)).findAll();
    }

    @Test
    void buy() throws Exception {
        when(orderService.buy()).thenReturn(orderInfo);

        mockMvc.perform(
                        post("/buy")
                                .param("newOrder", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        String.format("/orders/%d?newOrder=true", orderInfo.getId()))
                );

        verify(orderService, times(1)).buy();
    }
}