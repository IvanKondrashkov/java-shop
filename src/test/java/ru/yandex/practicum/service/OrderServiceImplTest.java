package ru.yandex.practicum.service;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.mapper.CartItemMapper;
import ru.yandex.practicum.repository.OrderRepository;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartItemService cartItemService;
    @InjectMocks
    private OrderServiceImpl orderService;
    private Item item;
    private Order order;
    private CartItem cartItem;


    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .title("Внешний SSD Samsung T7")
                .description("Portable SSD 1ТБ со скоростью передачи до 1050 МБ/с")
                .price(BigDecimal.valueOf(14999.00))
                .build();
        order = Order.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .totalSum(new BigDecimal("0.0"))
                .build();
        cartItem = CartItem.builder()
                .id(1L)
                .quantity(1)
                .itemId(item.getId())
                .orderId(order.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        order = null;
        cartItem = null;
    }

    @Test
    void findById() {
        when(orderRepository.findById(order.getId())).thenReturn(Mono.just(order));
        when(cartItemService.findAllByOrderId(order.getId())).thenReturn(
                Flux.just(CartItemMapper.cartItemToCartItemInfo(cartItem, item))
        );

        StepVerifier.create(orderService.findById(order.getId()))
                .expectNextMatches(newOrder ->
                        newOrder != null &&
                        newOrder.getId().equals(order.getId()) &&
                        newOrder.getTotalSum().equals(order.getTotalSum()) &&
                        newOrder.getItems().size() == 1
                )
                .verifyComplete();

        verify(orderRepository, times(1)).findById(order.getId());
        verify(cartItemService, times(1)).findAllByOrderId(order.getId());
    }

    @Test
    void findAll() {
        when(orderRepository.findAll()).thenReturn(Flux.just(order));
        when(cartItemService.findAllByOrderId(order.getId())).thenReturn(
                Flux.just(CartItemMapper.cartItemToCartItemInfo(cartItem, item))
        );

        StepVerifier.create(orderService.findAll().collectList())
                .expectNextMatches(orders -> orders != null && orders.size() == 1)
                .verifyComplete();

        verify(orderRepository, times(1)).findAll();
        verify(cartItemService, times(1)).findAllByOrderId(order.getId());
    }

    @Test
    void buy() {
        when(cartItemService.purchaseOrder()).thenReturn(
                Mono.just(OrderMapper.orderToOrderInfo(order, List.of(CartItemMapper.cartItemToCartItemInfo(cartItem, item))))
        );

        StepVerifier.create(orderService.buy())
                .expectNextMatches(newOrder ->
                        newOrder != null &&
                        newOrder.getId().equals(order.getId()) &&
                        newOrder.getTotalSum().equals(order.getTotalSum()) &&
                        newOrder.getItems().size() == 1
                )
                .verifyComplete();

        verify(cartItemService, times(1)).purchaseOrder();
    }
}