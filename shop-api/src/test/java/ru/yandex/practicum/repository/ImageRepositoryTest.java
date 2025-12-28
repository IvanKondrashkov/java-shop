package ru.yandex.practicum.repository;

import java.util.UUID;
import java.util.Random;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
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

        imageRepository.deleteAll().block();
    }

    @Test
    void findById() {
        Image imageDb = imageRepository.save(image).block();

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());

        StepVerifier.create(imageRepository.findById(imageDb.getId()))
                .expectNextMatches(newImageDb ->
                        newImageDb != null &&
                        newImageDb.getId() != null &&
                        newImageDb.getFileName().equals(image.getFileName()) &&
                        newImageDb.getImageUrl().equals(image.getImageUrl())
                )
                .verifyComplete();
    }

    @Test
    void findAll() {
        Image imageDb = imageRepository.save(image).block();

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());

        StepVerifier.create(imageRepository.findAll().collectList())
                .expectNextMatches(images -> images != null && images.size() == 1)
                .verifyComplete();
    }

    @Test
    void findByItemId() {
        Image imageDb = imageRepository.save(image).block();

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());

        StepVerifier.create(imageRepository.findByItemId(new Random().nextLong()))
                .verifyComplete();
    }

    @Test
    void save() {
        Image imageDb = imageRepository.save(image).block();

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());
    }

    @Test
    void deleteById() {
        Image imageDb = imageRepository.save(image).block();

        assertNotNull(imageDb);
        assertNotNull(imageDb.getId());

        StepVerifier.create(imageRepository.deleteById(imageDb.getId()).then(imageRepository.findById(imageDb.getId())))
                .verifyComplete();
    }
}