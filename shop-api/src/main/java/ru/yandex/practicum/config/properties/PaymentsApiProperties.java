package ru.yandex.practicum.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "payments.api")
public class PaymentsApiProperties {
    private String url;
}