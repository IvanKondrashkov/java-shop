package ru.yandex.practicum.repository;

import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.model.Image;
import static org.junit.jupiter.api.Assertions.*;

public class ImageRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private ImageRepository imageRepository;
    private Image image;

    @BeforeEach
    void setUp() {
        String fileName = UUID.randomUUID().toString();
        image = Image.builder()
                .fileName(fileName)
                .imageUrl("https://storage.yandexcloud.net/java-shop-image-storage/" + fileName)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @AfterEach
    void tearDown() {
        image = null;

        imageRepository.deleteAll();
    }

    @Test
    void findById() {
        Image imageDb = imageRepository.save(image);

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());

        imageDb = imageRepository.findById(imageDb.getId()).orElse(null);

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());
    }

    @Test
    void findAll() {
        Image imageDb = imageRepository.save(image);

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());

        List<Image> images = imageRepository.findAll();

        assertNotNull(images);
        assertEquals(images.size(), 1);
    }

    @Test
    void save() {
        Image imageDb = imageRepository.save(image);

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());
    }

    @Test
    void deleteById() {
        Image imageDb = imageRepository.save(image);

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());

        imageRepository.deleteById(imageDb.getId());
        imageDb = imageRepository.findById(imageDb.getId()).orElse(null);

        assertNull(imageDb);
    }
}