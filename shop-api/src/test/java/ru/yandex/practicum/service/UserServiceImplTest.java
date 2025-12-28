package ru.yandex.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.dto.Role;
import ru.yandex.practicum.model.User;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.repository.UserRepository;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest extends BaseServiceTest {
    @MockitoBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    private User user;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("Djon")
                .password("123456")
                .role(Role.USER.name())
                .enabled(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    void findById() {
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.findById(user.getId()))
                .expectNextMatches(newUser ->
                        newUser != null &&
                        newUser.getId().equals(user.getId()) &&
                        newUser.getUsername().equals(user.getUsername()) &&
                        newUser.getPassword() != null &&
                        newUser.getRole().equals(user.getRole()) &&
                        newUser.isEnabled()
                )
                .verifyComplete();

        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @WithMockUser(username = "Djon")
    void getCurrentUserId() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.getCurrentUserId())
                .expectNextMatches(userId -> userId.equals(user.getId()))
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void existsByUsername() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Mono.just(user));

        StepVerifier.create(userService.existsByUsername(user.getUsername()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    void register() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Mono.empty());
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.register(user))
                .expectNextMatches(newUser ->
                        newUser != null &&
                        newUser.getId().equals(user.getId()) &&
                        newUser.getUsername().equals(user.getUsername()) &&
                        newUser.getPassword() != null &&
                        newUser.getRole().equals(user.getRole()) &&
                        newUser.isEnabled()
                )
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, times(1)).save(user);
    }
}