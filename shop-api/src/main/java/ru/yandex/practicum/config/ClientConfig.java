package ru.yandex.practicum.config;

import ru.yandex.practicum.client.ApiClient;
import ru.yandex.practicum.client.api.BalanceApi;
import ru.yandex.practicum.client.api.PaymentsApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.yandex.practicum.config.properties.PaymentsApiProperties;

@Configuration
@EnableConfigurationProperties(PaymentsApiProperties.class)
public class ClientConfig {
    @Bean
    public ApiClient paymentApiClient(PaymentsApiProperties paymentsApiProperties) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(paymentsApiProperties.getUrl());
        return apiClient;
    }

    @Bean
    public BalanceApi balanceApi(ApiClient apiClient) {
        return new BalanceApi(apiClient);
    }

    @Bean
    public PaymentsApi paymentsApi(ApiClient apiClient) {
        return new PaymentsApi(apiClient);
    }
}