package ru.yandex.practicum.config;

import java.net.URI;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import ru.yandex.practicum.config.properties.AwsProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;

@Configuration
@EnableConfigurationProperties(AwsProperties.class)
public class S3Config {
    @Bean
    public SdkAsyncHttpClient asyncHttpClient() {
        return NettyNioAsyncHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(10))
                .maxConcurrency(100)
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient(SdkAsyncHttpClient asyncHttpClient, AwsProperties awsProperties) {
        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        return S3AsyncClient.builder()
                .httpClient(asyncHttpClient)
                .region(Region.of(awsProperties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                awsProperties.getAccessKey(),
                                awsProperties.getSecretKey()
                        )
                ))
                .serviceConfiguration(serviceConfiguration)
                .endpointOverride(URI.create(awsProperties.getServiceEndpoint()))
                .build();
    }
}