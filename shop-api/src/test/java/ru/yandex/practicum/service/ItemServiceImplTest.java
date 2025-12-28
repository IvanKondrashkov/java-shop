package ru.yandex.practicum.service;

import java.util.UUID;
import java.time.Duration;
import java.math.BigDecimal;
import java.util.Collections;
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
import ru.yandex.practicum.dto.SortType;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.CartItem;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.CartItemRepository;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserService userService;
    @Mock
    private CacheService cacheService;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Image image;
    private Item item;
    private User user;
    private CartItem cartItem;


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
        cartItem = CartItem.builder()
                .id(1L)
                .quantity(1)
                .userId(user.getId())
                .itemId(item.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        image = null;
        item = null;
        user = null;
        cartItem = null;
    }

    @Test
    void findById() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cacheService.save("item", item.getId().toString(), item, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("item", item.getId().toString(), Item.class)).thenReturn(Mono.just(item));
        when(cacheService.save("image", item.getId().toString(), image, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("image", item.getId().toString(), Image.class)).thenReturn(Mono.just(image));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));
        when(cartItemRepository.findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId())).thenReturn(Mono.just(cartItem));

        StepVerifier.create(itemService.findById(item.getId()))
                .expectNextMatches(newItem ->
                        newItem != null &&
                        newItem.getId().equals(item.getId()) &&
                        newItem.getTitle().equals(item.getTitle()) &&
                        newItem.getDescription().equals(item.getDescription()) &&
                        newItem.getPrice().compareTo(item.getPrice()) == 0 &&
                        newItem.getCount().equals(1)
                )
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(cacheService, times(1)).save("item", item.getId().toString(), item, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("item", item.getId().toString(), Item.class);
        verify(cacheService, times(1)).save("image", item.getId().toString(), image, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("image", item.getId().toString(), Image.class);
        verify(itemRepository, times(1)).findById(item.getId());
        verify(imageRepository, times(1)).findByItemId(item.getId());
        verify(cartItemRepository, times(1)).findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId());
    }

    @Test
    void findAll() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cacheService.getList(anyString(), any())).thenReturn(Mono.just(Collections.singletonList(item)));
        when(cacheService.save("image", item.getId().toString(), image, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("image", item.getId().toString(), Image.class)).thenReturn(Mono.just(image));
        when(itemRepository.findAll(10, 0, SortType.ID.getValue())).thenReturn(Flux.just(item));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));
        when(cartItemRepository.findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId())).thenReturn(Mono.just(cartItem));

        StepVerifier.create(itemService.findAll(10, 0, SortType.ID.getValue()).collectList())
                .expectNextMatches(items -> items != null && items.size() == 1)
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(cacheService, times(1)).getList(anyString(), any());
        verify(cacheService, times(1)).save("image", item.getId().toString(), image, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("image", item.getId().toString(), Image.class);
        verify(itemRepository, times(2)).findAll(10, 0, SortType.ID.getValue());
        verify(imageRepository, times(1)).findByItemId(item.getId());
        verify(cartItemRepository, times(1)).findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId());
    }

    @Test
    void findAllBySearch() {
        when(userService.getCurrentUserId()).thenReturn(Mono.just(1L));
        when(cacheService.getList(anyString(), any())).thenReturn(Mono.just(Collections.singletonList(item)));
        when(cacheService.save("image", item.getId().toString(), image, Duration.ofMinutes(10))).thenReturn(Mono.empty());
        when(cacheService.get("image", item.getId().toString(), Image.class)).thenReturn(Mono.just(image));
        when(itemRepository.findAllBySearch("SSD", "SSD",10, 0, SortType.ID.getValue())).thenReturn(Flux.just(item));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));
        when(cartItemRepository.findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId())).thenReturn(Mono.just(cartItem));

        StepVerifier.create(itemService.findAllBySearch("SSD", 10, 0, SortType.ID.getValue()).collectList())
                .expectNextMatches(items -> items != null && items.size() == 1)
                .verifyComplete();

        verify(userService, times(1)).getCurrentUserId();
        verify(cacheService, times(1)).getList(anyString(), any());
        verify(cacheService, times(1)).save("image", item.getId().toString(), image, Duration.ofMinutes(10));
        verify(cacheService, times(1)).get("image", item.getId().toString(), Image.class);
        verify(itemRepository, times(1)).findAllBySearch("SSD", "SSD",10, 0, SortType.ID.getValue());
        verify(imageRepository, times(1)).findByItemId(item.getId());
        verify(cartItemRepository, times(1)).findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), user.getId());
    }

    @Test
    void count() {
        when(itemRepository.count()).thenReturn(Mono.just(1L));

        StepVerifier.create(itemService.count())
                .expectNext(1L)
                .verifyComplete();

        verify(itemRepository, times(1)).count();
    }

    @Test
    void countBySearch() {
        when(itemRepository.countBySearch("SSD", "SSD")).thenReturn(Mono.just(1L));

        StepVerifier.create(itemService.countBySearch("SSD"))
                .expectNext(1L)
                .verifyComplete();

        verify(itemRepository, times(1)).countBySearch("SSD", "SSD");
    }
}