package ru.yandex.practicum.repository;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Image;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
                .image(image)
                .build();

        imageRepository.save(image);

    }

    @AfterEach
    void tearDown() {
        image = null;
        item = null;

        itemRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void findById() {
        Item itemDb = itemRepository.save(item);

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        itemDb = itemRepository.findById(itemDb.getId()).orElse(null);

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());
    }

    @Test
    void findAll() {
        Item itemDb = itemRepository.save(item);

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        List<Item> items = itemRepository.findAll();

        assertNotNull(items);
        assertEquals(items.size(), 1);
    }

    @Test
    void findAllBySearch() {
        Item itemDb = itemRepository.save(item);

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Item> page = itemRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("SSD", "SSD", pageRequest);

        assertNotNull(page.getContent());
        assertEquals(page.getContent().size(), 1);
    }

    @Test
    void save() {
        Item itemDb = itemRepository.save(item);

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());
    }

    @Test
    void deleteById() {
        Item itemDb = itemRepository.save(item);

        assertNotNull(itemDb);
        assertNotNull(itemDb.getId());

        itemRepository.deleteById(itemDb.getId());
        itemDb = itemRepository.findById(itemDb.getId()).orElse(null);

        assertNull(itemDb);
    }
}