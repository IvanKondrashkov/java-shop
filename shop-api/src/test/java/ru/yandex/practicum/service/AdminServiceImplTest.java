package ru.yandex.practicum.service;

import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.repository.*;
import org.springframework.core.io.ClassPathResource;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import java.io.IOException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest extends BaseServiceTest {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private S3AsyncClient s3Client;
    private byte[] bytes;

    @BeforeEach
    void setUp() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/items.csv");
        bytes = Files.readAllBytes(resource.getFile().toPath());

        Mono.fromFuture(() -> s3Client.createBucket(
                CreateBucketRequest.builder()
                        .bucket("test-bucket")
                        .build()
        )).block();
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll().block();
        imageRepository.deleteAll().block();
    }

    @Test
    void importCsvFile() {
        FilePart filePart = mock(FilePart.class);
        DataBuffer dataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(bytes);

        when(filePart.content()).thenReturn(Flux.just(dataBuffer));

        adminService.importCsvFile(filePart).block();


        StepVerifier.create(imageRepository.count())
                .expectNext(10L)
                .verifyComplete();

        StepVerifier.create(itemRepository.count())
                .expectNext(10L)
                .verifyComplete();
    }
}