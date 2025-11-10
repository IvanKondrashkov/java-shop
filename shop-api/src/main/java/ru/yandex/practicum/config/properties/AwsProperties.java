package ru.yandex.practicum.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
    private String serviceEndpoint;
    private String region;
    private String bucketName;
    private String accessKey;
    private String secretKey;
}