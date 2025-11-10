package ru.yandex.practicum.service;

import java.util.Set;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.Collections;
import java.time.LocalDateTime;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.dto.OrderInfo;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.OrderRepository;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private CartItemService cartItemService;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderServiceImpl orderService;
    private Order order;


    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .totalSum(new BigDecimal("0.0"))
                .items(Set.of())
                .build();
    }

    @AfterEach
    void tearDown() {
        order = null;
    }

    @Test
    void findById() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderInfo orderDb = orderService.findById(order.getId());

        assertNotNull(orderDb);
        assertEquals(orderDb.getTotalSum(), order.getTotalSum());
        assertEquals(orderDb.getItems().size(), 0);

        verify(orderRepository, times(1)).findById(order.getId());
    }

    @Test
    void findAll() {
        when(orderRepository.findAll()).thenReturn(Collections.singletonList(order));

        List<OrderInfo> orders = orderService.findAll();

        assertNotNull(orders);
        assertEquals(orders.size(), 1);

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void buy() {
        when(cartItemService.purchaseOrder()).thenReturn(OrderMapper.orderToOrderInfo(order));

        OrderInfo orderDb = orderService.buy();

        assertNotNull(orderDb);
        assertEquals(orderDb.getTotalSum(), order.getTotalSum());
        assertEquals(orderDb.getItems().size(), 0);

        verify(cartItemService, times(1)).purchaseOrder();
    }
}