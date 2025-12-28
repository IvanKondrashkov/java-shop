package ru.yandex.practicum.service;

import java.util.*;
import java.time.Duration;
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
import ru.yandex.practicum.dto.Role;
import ru.yandex.practicum.model.*;
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
    private UserService userService;
    @Mock
    private CacheService cacheService;
    @Mock
    private PaymentClient paymentClient;
    @InjectMocks
    private CartItemServiceImpl cartItemService;
    private Image image;
    private Item item;
    private User user;
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

        paymentResponse = new PaymentResponse();
        paymentResponse.setUserId(user.getId());
        paymentResponse.setStatus(PaymentResponse.StatusEnum.SUCCESS);
    }

    @AfterEach
    void tearDown() {
        image = null;
        item = null;
        user = null;
        order = null;
        cartItem = null;
        paymentResponse = null;
    }

    @Test
    void findAll() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cacheService.save("item", item.getId().toString(), item, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("item", item.getId().toString(), Item.class)).thenReturn(Mono.just(item));
        when(cacheService.save("image", item.getId().toString(), image, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("image", item.getId().toString(), Image.class)).thenReturn(Mono.just(image));
        when(cartItemRepository.findAllByUserIdAndOrderIdIsNull(user.getId())).thenReturn(Flux.just(cartItem));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));

        StepVerifier.create(cartItemService.findAll().collectList())
                .expectNextMatches(cartItems -> cartItems != null && cartItems.size() == 1)
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(cacheService, times(1)).save("item", item.getId().toString(), item, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("item", item.getId().toString(), Item.class);
        verify(cacheService, times(1)).save("image", item.getId().toString(), image, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("image", item.getId().toString(), Image.class);
        verify(cartItemRepository, times(1)).findAllByUserIdAndOrderIdIsNull(user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(imageRepository, times(1)).findByItemId(item.getId());
    }

    @Test
    void findAllByOrderId() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cacheService.save("item", item.getId().toString(), item, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("item", item.getId().toString(), Item.class)).thenReturn(Mono.just(item));
        when(cartItemRepository.findAllByOrderIdAndUserId(order.getId(), user.getId())).thenReturn(Flux.just(cartItem));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));

        StepVerifier.create(cartItemService.findAllByOrderId(order.getId()).collectList())
                .expectNextMatches(cartItems -> cartItems != null && cartItems.size() == 1)
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(cacheService, times(1)).save("item", item.getId().toString(), item, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("item", item.getId().toString(), Item.class);
        verify(cartItemRepository, times(1)).findAllByOrderIdAndUserId(order.getId(), user.getId());
        verify(itemRepository, times(1)).findById(item.getId());
    }

    @Test
    void purchaseItem() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cacheService.save("item", item.getId().toString(), item, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("item", item.getId().toString(), Item.class)).thenReturn(Mono.just(item));
        when(cacheService.save("image", item.getId().toString(), image, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("image", item.getId().toString(), Image.class)).thenReturn(Mono.just(image));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));
        when(cartItemRepository.findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId())).thenReturn(Mono.just(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(Mono.just(cartItem));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));

        StepVerifier.create(cartItemService.purchaseItem(item.getId(), Action.PLUS))
                .expectNextMatches(newItem ->
                        newItem != null &&
                        newItem.getId().equals(item.getId()) &&
                        newItem.getTitle().equals(item.getTitle()) &&
                        newItem.getDescription().equals(item.getDescription()) &&
                        newItem.getPrice().compareTo(item.getPrice()) == 0 &&
                        newItem.getCount().equals(2)
                )
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(cacheService, times(1)).save("item", item.getId().toString(), item, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("item", item.getId().toString(), Item.class);
        verify(cacheService, times(1)).save("image", item.getId().toString(), image, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("image", item.getId().toString(), Image.class);
        verify(itemRepository, times(1)).findById(item.getId());
        verify(cartItemRepository, times(1)).findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(imageRepository, times(1)).findByItemId(item.getId());
    }

    @Test
    void purchaseOrder() {
        when(cacheService.save("item", item.getId().toString(), item, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("item", item.getId().toString(), Item.class)).thenReturn(Mono.just(item));
        when(cartItemRepository.findAllByUserIdAndOrderIdIsNull(user.getId())).thenReturn(Flux.just(cartItem));
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

        verify(cacheService, times(2)).save("item", item.getId().toString(), item, Duration.ofMinutes(10));
        verify(cacheService, times(2)).get("item", item.getId().toString(), Item.class);
        verify(cartItemRepository, times(1)).findAllByUserIdAndOrderIdIsNull(user.getId());
        verify(itemRepository, atLeastOnce()).findById(item.getId());
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(cartItemRepository, times(1)).saveAll(anyIterable());
        verify(paymentClient, times(1)).processPayment(any(PaymentRequest.class));
    }

    @Test
    void deleteById() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cacheService.save("item", item.getId().toString(), item, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("item", item.getId().toString(), Item.class)).thenReturn(Mono.just(item));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));
        when(cartItemRepository.findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId())).thenReturn(Mono.just(cartItem));
        when(cartItemRepository.deleteByItemIdAndUserId(item.getId(), user.getId())).thenReturn(Mono.empty());

        StepVerifier.create(cartItemService.deleteById(item.getId(), Action.DELETE))
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(cacheService, times(1)).save("item", item.getId().toString(), item, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("item", item.getId().toString(), Item.class);
        verify(itemRepository, times(1)).findById(item.getId());
        verify(cartItemRepository, times(1)).findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId());
        verify(cartItemRepository, times(1)).deleteByItemIdAndUserId(item.getId(), user.getId());
    }
}