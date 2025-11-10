package ru.yandex.practicum.service;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseServiceTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("test")
            .withUsername("root")
            .withPassword("root")
            .withReuse(true);

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.3.2"))
            .withServices(S3)
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.service-endpoint", () -> localStack.getEndpointOverride(S3).toString());
        registry.add("aws.region", localStack::getRegion);
        registry.add("aws.bucket-name", () -> "test-bucket");
        registry.add("aws.access-key", localStack::getAccessKey);
        registry.add("aws.secret-key", localStack::getSecretKey);

        registry.add("spring.datasource.hikari.maximum-pool-size", () -> "5");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "30000");
        registry.add("spring.datasource.hikari.max-lifetime", () -> "30000");
    }
}