package ru.yandex.practicum.service;

import java.util.*;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminServiceImplTest extends BaseServiceTest {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private AdminServiceImpl adminService;
    @Autowired
    private AmazonS3 s3Client;
    private byte[] bytes;

    @BeforeEach
    void setUp() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/items.csv");
        bytes = Files.readAllBytes(resource.getFile().toPath());

        s3Client.createBucket("test-bucket");
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
        imageRepository.deleteAll();
    }

    @Test
    void importCsvFile() {
        adminService.importCsvFile(new MockMultipartFile("file", bytes));

        List<Image> images = imageRepository.findAll();
        List<Item> items = itemRepository.findAll();

        assertNotNull(images);
        assertEquals(images.size(), 10);

        assertNotNull(items);
        assertEquals(items.size(), 10);
    }
}