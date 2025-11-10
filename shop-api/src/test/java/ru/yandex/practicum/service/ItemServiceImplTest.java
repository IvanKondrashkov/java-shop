package ru.yandex.practicum.service;

import java.util.UUID;
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
import ru.yandex.practicum.dto.SortType;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;
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
    private CacheService cacheService;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Image image;
    private Item item;


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
    }

    @AfterEach
    void tearDown() {
        image = null;
        item = null;
    }

    @Test
    void findById() {
        when(cacheService.addItem(item)).thenReturn(Mono.empty());
        when(cacheService.getItem(item.getId().toString())).thenReturn(Mono.just(item));
        when(cacheService.addImage(item.getId().toString(), image)).thenReturn(Mono.empty());
        when(cacheService.getImage(item.getId().toString())).thenReturn(Mono.just(image));
        when(itemRepository.findById(item.getId())).thenReturn(Mono.just(item));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));
        when(cartItemRepository.countByItemId(item.getId())).thenReturn(Mono.just(1));

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

        verify(cacheService, times(1)).addItem(item);
        verify(cacheService, times(1)).getItem(item.getId().toString());
        verify(cacheService, times(1)).addImage(item.getId().toString(), image);
        verify(cacheService, times(1)).getImage(item.getId().toString());
        verify(itemRepository, times(1)).findById(item.getId());
        verify(imageRepository, times(1)).findByItemId(item.getId());
        verify(cartItemRepository, times(1)).countByItemId(item.getId());
    }

    @Test
    void findAll() {
        when(cacheService.getItems(anyString())).thenReturn(Mono.just(Collections.singletonList(item)));
        when(cacheService.addImage(item.getId().toString(), image)).thenReturn(Mono.empty());
        when(cacheService.getImage(item.getId().toString())).thenReturn(Mono.just(image));
        when(itemRepository.findAll(10, 0, SortType.ID.getValue())).thenReturn(Flux.just(item));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));
        when(cartItemRepository.countByItemId(item.getId())).thenReturn(Mono.just(1));

        StepVerifier.create(itemService.findAll(10, 0, SortType.ID.getValue()).collectList())
                .expectNextMatches(items -> items != null && items.size() == 1)
                .verifyComplete();

        verify(cacheService, times(1)).getItems(anyString());
        verify(cacheService, times(1)).addImage(item.getId().toString(), image);
        verify(cacheService, times(1)).getImage(item.getId().toString());
        verify(itemRepository, times(2)).findAll(10, 0, SortType.ID.getValue());
        verify(imageRepository, times(1)).findByItemId(item.getId());
        verify(cartItemRepository, times(1)).countByItemId(item.getId());
    }

    @Test
    void findAllBySearch() {
        when(cacheService.getItemsSearch(anyString())).thenReturn(Mono.just(Collections.singletonList(item)));
        when(cacheService.addImage(item.getId().toString(), image)).thenReturn(Mono.empty());
        when(cacheService.getImage(item.getId().toString())).thenReturn(Mono.just(image));
        when(itemRepository.findAllBySearch("SSD", "SSD",10, 0, SortType.ID.getValue())).thenReturn(Flux.just(item));
        when(imageRepository.findByItemId(item.getId())).thenReturn(Mono.just(image));
        when(cartItemRepository.countByItemId(item.getId())).thenReturn(Mono.just(1));

        StepVerifier.create(itemService.findAllBySearch("SSD", 10, 0, SortType.ID.getValue()).collectList())
                .expectNextMatches(items -> items != null && items.size() == 1)
                .verifyComplete();

        verify(cacheService, times(1)).getItemsSearch(anyString());
        verify(cacheService, times(1)).addImage(item.getId().toString(), image);
        verify(cacheService, times(1)).getImage(item.getId().toString());
        verify(itemRepository, times(1)).findAllBySearch("SSD", "SSD",10, 0, SortType.ID.getValue());
        verify(imageRepository, times(1)).findByItemId(item.getId());
        verify(cartItemRepository, times(1)).countByItemId(item.getId());
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