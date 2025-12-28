package ru.yandex.practicum.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.Role;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.service.UserService;
import static org.mockito.Mockito.*;

public class UserControllerTest extends BaseControllerTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithAnonymousUser
    void formToRegister() {
        webTestClient.get()
                .uri("/users/register")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithAnonymousUser
    void register() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", "john_doe");
        formData.add("password", "12345678");
        formData.add("confirmPassword", "12345678");

        when(passwordEncoder.encode("12345678")).thenReturn("12345678");
        when(userService.register(any(User.class))).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/users/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(userService, times(1)).register(argThat(user ->
                user.getUsername().equals("john_doe") &&
                user.getPassword().equals("12345678") &&
                user.getRole().equals(Role.USER.name()) &&
                user.isEnabled()
        ));
    }
}