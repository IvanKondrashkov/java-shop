package ru.yandex.practicum.repository;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import ru.yandex.practicum.dto.SortType;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Image;
import static org.junit.jupiter.api.Assertions.*;

public class ItemRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Image image;
    private Item item;

    @BeforeEach
    void setUp() {
        String fileName = UUID.randomUUID().toString();
        image = Image.builder()
                .fileName(fileName)
                .imageUrl("https://storage.yandexcloud.net/java-shop-image-storage/" + fileName)
                .createdAt(LocalDateTime.now())
                .build();
        item = Item.builder()
                .title("Внешний SSD Samsung T7")
                .description("Portable SSD 1ТБ со скоростью передачи до 1050 МБ/с")
                .price(BigDecimal.valueOf(14999.00))
                .build();

        imageRepository.save(image)
                .doOnNext(newImage -> item.setImageId(newImage.getId()))
                .block();

    }

    @AfterEach
    void tearDown() {
        image = null;
        item = null;

        itemRepository.deleteAll().block();
        imageRepository.deleteAll().block();
    }

    @Test
    void findById() {
        Item itemDb = itemRepository.save(item).block();

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        StepVerifier.create(itemRepository.findById(itemDb.getId()))
                .expectNextMatches(newItemDb ->
                        newItemDb != null &&
                        newItemDb.getId() != null &&
                        newItemDb.getTitle().equals(item.getTitle()) &&
                        newItemDb.getDescription().equals(item.getDescription()) &&
                        newItemDb.getPrice().compareTo(item.getPrice()) == 0)
                .verifyComplete();
    }

    @Test
    void findAll() {
        Item itemDb = itemRepository.save(item).block();

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        StepVerifier.create(itemRepository.findAll(10, 0, SortType.ID.getValue()).collectList())
                .expectNextMatches(items -> items != null && items.size() == 1)
                .verifyComplete();
    }

    @Test
    void findAllBySearch() {
        Item itemDb = itemRepository.save(item).block();

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        StepVerifier.create(itemRepository.findAllBySearch("SSD", "SSD", 10, 0, SortType.ID.getValue()).collectList())
                .expectNextMatches(items -> items != null && items.size() == 1)
                .verifyComplete();
    }

    @Test
    void count() {
        Item itemDb = itemRepository.save(item).block();

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        StepVerifier.create(itemRepository.count())
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void countBySearch() {
        Item itemDb = itemRepository.save(item).block();

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        StepVerifier.create(itemRepository.countBySearch("SSD", "SSD"))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void save() {
        Item itemDb = itemRepository.save(item).block();

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());
    }

    @Test
    void deleteById() {
        Item itemDb = itemRepository.save(item).block();

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        StepVerifier.create(itemRepository.deleteById(itemDb.getId())
                        .then(itemRepository.findById(itemDb.getId()))
                )
                .verifyComplete();
    }
}