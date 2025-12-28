package ru.yandex.practicum.controller;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BaseControllerTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("test")
            .withUsername("root")
            .withPassword("root")
            .withReuse(true)
            .withInitScripts(
                    "./db/migrations/01-images-create.sql",
                    "./db/migrations/02-items-create.sql",
                    "./db/migrations/03-users-create.sql",
                    "./db/migrations/04-orders-create.sql",
                    "./db/migrations/05-cart-items-create.sql"
            );

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
    }
}