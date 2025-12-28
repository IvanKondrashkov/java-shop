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
import ru.yandex.practicum.dto.Role;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.mapper.CartItemMapper;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.OrderRepository;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartItemService cartItemService;
    @Mock
    private UserService userService;
    @InjectMocks
    private OrderServiceImpl orderService;
    private Item item;
    private User user;
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
        user = User.builder()
                .id(1L)
                .username("Djon")
                .password("123456")
                .role(Role.USER.name())
                .enabled(true)
                .build();
        order = Order.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .totalSum(BigDecimal.valueOf(14999.00))
                .userId(user.getId())
                .build();
        cartItem = CartItem.builder()
                .id(1L)
                .quantity(1)
                .userId(user.getId())
                .itemId(item.getId())
                .orderId(order.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        item = null;
        user = null;
        order = null;
        cartItem = null;
    }

    @Test
    void findById() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(orderRepository.findByIdAndUserId(order.getId(), user.getId())).thenReturn(Mono.just(order));
        when(cartItemService.findAllByOrderId(order.getId())).thenReturn(
                Flux.just(CartItemMapper.cartItemToCartItemInfo(cartItem, item))
        );

        StepVerifier.create(orderService.findById(order.getId()))
                .expectNextMatches(newOrder ->
                        newOrder != null &&
                        newOrder.getId().equals(order.getId()) &&
                        newOrder.getTotalSum().compareTo(order.getTotalSum()) == 0 &&
                        newOrder.getItems().size() == 1
                )
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(orderRepository, times(1)).findByIdAndUserId(order.getId(), user.getId());
        verify(cartItemService, times(1)).findAllByOrderId(order.getId());
    }

    @Test
    void findAll() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(orderRepository.findAllByUserId(user.getId())).thenReturn(Flux.just(order));
        when(cartItemService.findAllByOrderId(order.getId())).thenReturn(
                Flux.just(CartItemMapper.cartItemToCartItemInfo(cartItem, item))
        );

        StepVerifier.create(orderService.findAll().collectList())
                .expectNextMatches(orders -> orders != null && orders.size() == 1)
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(orderRepository, times(1)).findAllByUserId(user.getId());
        verify(cartItemService, times(1)).findAllByOrderId(order.getId());
    }

    @Test
    void buy() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cartItemService.purchaseOrder(user.getId())).thenReturn(
                Mono.just(OrderMapper.orderToOrderInfo(order, List.of(CartItemMapper.cartItemToCartItemInfo(cartItem, item))))
        );

        StepVerifier.create(orderService.buy())
                .expectNextMatches(newOrder ->
                        newOrder != null &&
                        newOrder.getId().equals(order.getId()) &&
                        newOrder.getTotalSum().compareTo(order.getTotalSum()) == 0 &&
                        newOrder.getItems().size() == 1
                )
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(cartItemService, times(1)).purchaseOrder(user.getId());
    }
}