package ru.yandex.practicum.service;

import java.util.UUID;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.services.s3.AmazonS3;
import ru.yandex.practicum.dto.response.ImageInfo;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class S3ServiceImplTest extends BaseServiceTest {
    @Autowired
    private S3ServiceImpl s3Service;
    @Autowired
    private AmazonS3 s3Client;
    private byte[] bytes;

    @BeforeEach
    void setUp() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/test-image.jpg");
        bytes = Files.readAllBytes(resource.getFile().toPath());

        s3Client.createBucket("test-bucket");
    }

    @Test
    void uploadImage() {
        String fileName = UUID.randomUUID().toString();
        ImageInfo image = s3Service.uploadImage(fileName, bytes);

        assertNotNull(image);
        assertNotNull(image.getImageUrl());
        assertEquals(image.getFileName(), fileName);
    }
}