package ru.yandex.practicum.service;

import java.util.UUID;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import reactor.core.publisher.Mono;
import org.springframework.core.io.ClassPathResource;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class S3ServiceImplTest extends BaseServiceTest {
    @Autowired
    private S3Service s3Service;
    @Autowired
    private S3AsyncClient s3Client;
    private byte[] bytes;

    @BeforeEach
    void setUp() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/test-image.jpg");
        bytes = Files.readAllBytes(resource.getFile().toPath());

        Mono.fromFuture(() -> s3Client.createBucket(
                CreateBucketRequest.builder()
                        .bucket("test-bucket")
                        .build()
        )).block();
    }

    @Test
    void uploadImage() {
        String fileName = UUID.randomUUID().toString();

        StepVerifier.create(s3Service.uploadImage(fileName, bytes))
                .assertNext(image -> {
                    assertNotNull(image);
                    assertNotNull(image.getImageUrl());
                    assertEquals(fileName, image.getFileName());
                    assertTrue(image.getImageUrl().contains(fileName));
                })
                .verifyComplete();
    }
}