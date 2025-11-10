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
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.dto.ItemInfo;
import ru.yandex.practicum.dto.OrderInfo;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.repository.OrderRepository;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private CartItemServiceImpl cartItemService;
    private Item item;
    private CartItem cartItem;
    private Order order;



    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .title("Внешний SSD Samsung T7")
                .description("Portable SSD 1ТБ со скоростью передачи до 1050 МБ/с")
                .price(BigDecimal.valueOf(14999.00))
                .build();
        cartItem = CartItem.builder()
                .id(1L)
                .quantity(1)
                .item(item)
                .build();
        order = Order.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .totalSum(BigDecimal.valueOf(14999.00))
                .items(Set.of(cartItem))
                .build();
    }

    @AfterEach
    void tearDown() {
        item = null;
        cartItem = null;
        order = null;
    }

    @Test
    void findAll() {
        when(cartItemRepository.findAllByOrderIsNull()).thenReturn(Collections.singletonList(cartItem));
        when(cartItemRepository.countByItem_Id(item.getId())).thenReturn(1);

        List<ItemInfo> items = cartItemService.findAll();

        assertNotNull(items);
        assertEquals(items.size(), 1);

        verify(cartItemRepository, times(1)).findAllByOrderIsNull();
        verify(cartItemRepository, times(1)).countByItem_Id(item.getId());
    }

    @Test
    void purchaseItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(cartItemRepository.findByItem_Id(item.getId())).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        ItemInfo itemDb = cartItemService.purchaseItem(item.getId(), Action.PLUS);

        assertNotNull(itemDb);
        assertEquals(itemDb.getTitle(), item.getTitle());
        assertEquals(itemDb.getDescription(), item.getDescription());
        assertEquals(itemDb.getPrice(), item.getPrice());
        assertEquals(itemDb.getCount(), 2);

        verify(itemRepository, times(1)).findById(item.getId());
        verify(cartItemRepository, times(1)).findByItem_Id(item.getId());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void purchaseOrder() {
        when(cartItemRepository.findAllByOrderIsNull()).thenReturn(Collections.singletonList(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderInfo orderDb = cartItemService.purchaseOrder();

        assertNotNull(orderDb);
        assertEquals(orderDb.getTotalSum(), item.getPrice());
        assertEquals(orderDb.getItems().size(), 1);

        verify(cartItemRepository, times(1)).findAllByOrderIsNull();
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartItemRepository, times(1)).deleteAll(anyIterable());
    }

    @Test
    void deleteById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(cartItemRepository.findByItem_Id(item.getId())).thenReturn(Optional.of(cartItem));

        cartItemService.deleteById(item.getId(), Action.DELETE);

        verify(itemRepository, times(1)).findById(item.getId());
        verify(cartItemRepository, times(1)).findByItem_Id(item.getId());
        verify(cartItemRepository, times(1)).deleteByItem_Id(item.getId());
    }
}