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
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.model.PaymentRequest;
import ru.yandex.practicum.client.model.PaymentResponse;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.repository.OrderRepository;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceImplTest {
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CacheService cacheService;
    @Mock
    private PaymentClient paymentClient;
    @InjectMocks
    private CartItemServiceImpl cartItemService;
    private Image image;
    private Item item;
    private Order order;
    private CartItem cartItem;
    private PaymentResponse paymentResponse;


    @BeforeEach
    void setUp() {
        String fileName = UUID.randomUUID().toString();
        image = Image.builder()
                .id(1L)
                .fileName(fileName)
                .imageUrl("https://storage.yandexcloud.net/java-shop-image-storage/" + fileName)
                .createdAt(LocalDateTime.now())
                .build();
        item = Item.builder()
                .id(1L)
                .title("Внешний SSD Samsung T7")
                .description("Portable SSD 1ТБ со скоростью передачи до 1050 МБ/с")
                .price(BigDecimal.valueOf(14999.00))
                .imageId(image.getId())
                .build();
        order = Order.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .totalSum(BigDecimal.valueOf(14999.00))
                .build();
        cartItem = CartItem.builder()
                .id(1L)
                .quantity(1)
                .itemId(item.getId())
                .orderId(order.getId())
                .build();

        paymentResponse = new PaymentResponse();
        paymentResponse.setUserId(1L);
        paymentResponse.setStatus(PaymentResponse.StatusEnum.SUCCESS);
    }

    @AfterEach
    void tearDown() {
        image = null;
        item = null;
        order = null;
        cartItem = null;
        paymentResponse = null;
    }

    @Test
    void findAll() {
        when(cacheService.addItem(item)).thenReturn(Mono.empty());
        when(cacheService.getItem(item.getId().toString())).thenReturn(Mono.just(item));
        when(cacheService.addImage(item.getId().toString(), image)).thenReturn(Mono.empty());
        when(cacheService.getImage(item.getId().toString())).thenReturn(Mono.just(image));
        when(cartItemRepository.findAllByOrderIdIsNull()).thenReturn(Flux.just(cartItem));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));
        when(cartItemRepository.countByItemId(item.getId())).thenReturn(Mono.just(1));

        StepVerifier.create(cartItemService.findAll().collectList())
                .expectNextMatches(cartItems -> cartItems != null && cartItems.size() == 1)
                .verifyComplete();

        verify(cacheService, times(1)).addItem(item);
        verify(cacheService, times(1)).getItem(item.getId().toString());
        verify(cacheService, times(1)).addImage(item.getId().toString(), image);
        verify(cacheService, times(1)).getImage(item.getId().toString());
        verify(cartItemRepository, times(1)).findAllByOrderIdIsNull();
        verify(itemRepository, times(1)).findById(item.getId());
        verify(imageRepository, times(1)).findByItemId(item.getId());
        verify(cartItemRepository, times(1)).countByItemId(item.getId());
    }

    @Test
    void findAllByOrderId() {
        when(cacheService.addItem(item)).thenReturn(Mono.empty());
        when(cacheService.getItem(item.getId().toString())).thenReturn(Mono.just(item));
        when(cartItemRepository.findAllByOrderId(order.getId())).thenReturn(Flux.just(cartItem));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));

        StepVerifier.create(cartItemService.findAllByOrderId(order.getId()).collectList())
                .expectNextMatches(cartItems -> cartItems != null && cartItems.size() == 1)
                .verifyComplete();

        verify(cacheService, times(1)).addItem(item);
        verify(cacheService, times(1)).getItem(item.getId().toString());
        verify(cartItemRepository, times(1)).findAllByOrderId(order.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void purchaseItem() {
        when(cacheService.addItem(item)).thenReturn(Mono.empty());
        when(cacheService.getItem(item.getId().toString())).thenReturn(Mono.just(item));
        when(cacheService.addImage(item.getId().toString(), image)).thenReturn(Mono.empty());
        when(cacheService.getImage(item.getId().toString())).thenReturn(Mono.just(image));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));
        when(cartItemRepository.findByItemIdAndOrderIdIsNull(item.getId())).thenReturn(Mono.just(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(Mono.just(cartItem));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));
        when(cartItemRepository.countByItemId(item.getId())).thenReturn(Mono.just(1));


        StepVerifier.create(cartItemService.purchaseItem(item.getId(), Action.PLUS))
                .expectNextMatches(newItem ->
                        newItem != null &&
                        newItem.getId().equals(item.getId()) &&
                        newItem.getTitle().equals(item.getTitle()) &&
                        newItem.getDescription().equals(item.getDescription()) &&
                        newItem.getPrice().compareTo(item.getPrice()) == 0 &&
                        newItem.getCount().equals(1)
                )
                .verifyComplete();

        verify(cacheService, times(1)).addItem(item);
        verify(cacheService, times(1)).getItem(item.getId().toString());
        verify(cacheService, times(1)).addImage(item.getId().toString(), image);
        verify(cacheService, times(1)).getImage(item.getId().toString());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(cartItemRepository, times(1)).findByItemIdAndOrderIdIsNull(item.getId());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(imageRepository, times(1)).findByItemId(item.getId());
        verify(cartItemRepository, times(1)).countByItemId(item.getId());
    }

    @Test
    void purchaseOrder() {
        when(cacheService.addItem(item)).thenReturn(Mono.empty());
        when(cacheService.getItem(item.getId().toString())).thenReturn(Mono.just(item));
        when(cartItemRepository.findAllByOrderIdIsNull()).thenReturn(Flux.just(cartItem));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));
        when(cartItemRepository.saveAll(anyIterable())).thenReturn(Flux.just(cartItem));
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(Mono.just(paymentResponse));

        StepVerifier.create(cartItemService.purchaseOrder(1L))
                .expectNextMatches(newOrder ->
                        newOrder != null &&
                        newOrder.getId().equals(order.getId()) &&
                        newOrder.getTotalSum().compareTo(order.getTotalSum()) == 0 &&
                        newOrder.getItems().size() == 1
                )
                .verifyComplete();

        verify(cacheService, times(2)).addItem(item);
        verify(cacheService, times(2)).getItem(item.getId().toString());
        verify(cartItemRepository, times(1)).findAllByOrderIdIsNull();
        verify(itemRepository, atLeastOnce()).findById(item.getId());
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(cartItemRepository, times(1)).saveAll(anyIterable());
        verify(paymentClient, times(1)).processPayment(any(PaymentRequest.class));
    }

    @Test
    void deleteById() {
        when(cacheService.addItem(item)).thenReturn(Mono.empty());
        when(cacheService.getItem(item.getId().toString())).thenReturn(Mono.just(item));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));
        when(cartItemRepository.findByItemIdAndOrderIdIsNull(item.getId())).thenReturn(Mono.just(cartItem));
        when(cartItemRepository.deleteByItemId(item.getId())).thenReturn(Mono.empty());

        StepVerifier.create(cartItemService.deleteById(item.getId(), Action.DELETE))
                .verifyComplete();

        verify(cacheService, times(1)).addItem(item);
        verify(cacheService, times(1)).getItem(item.getId().toString());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(cartItemRepository, times(1)).findByItemIdAndOrderIdIsNull(item.getId());
        verify(cartItemRepository, times(1)).deleteByItemId(item.getId());
    }
}