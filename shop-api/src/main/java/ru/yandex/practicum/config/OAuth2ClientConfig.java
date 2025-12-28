package ru.yandex.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import ru.yandex.practicum.config.properties.PaymentsApiProperties;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
@EnableConfigurationProperties(PaymentsApiProperties.class)
public class OAuth2ClientConfig {
    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository(PaymentsApiProperties paymentsApiProperties) {
        ClientRegistration clientRegistration = ClientRegistration
                .withRegistrationId("shop-api")
                .clientId(paymentsApiProperties.getClientId())
                .clientSecret(paymentsApiProperties.getClientSecret())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri(paymentsApiProperties.getTokenUri())
                .build();
        return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientService authorizedClientService(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    ReactiveOAuth2AuthorizedClientManager oauth2AuthorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);

        manager.setAuthorizedClientProvider(ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .refreshToken()
                .build()
        );
        return manager;
    }

    @Bean
    public WebClient oauth2WebClient(ReactiveOAuth2AuthorizedClientManager oauth2AuthorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(oauth2AuthorizedClientManager);
        oauth2Filter.setDefaultClientRegistrationId("shop-api");

        return WebClient.builder()
                .filter(oauth2Filter)
                .build();
    }
}