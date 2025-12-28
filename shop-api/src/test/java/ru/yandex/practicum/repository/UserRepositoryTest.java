package ru.yandex.practicum.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import ru.yandex.practicum.dto.Role;
import ru.yandex.practicum.model.User;
import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("Djon")
                .password("123456")
                .role(Role.USER.name())
                .enabled(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        user = null;

        userRepository.deleteAll().block();
    }

    @Test
    void findById() {
        User userDb = userRepository.save(user).block();

        assertNotNull(userDb);
        assertNotNull(userDb.getId());

        StepVerifier.create(userRepository.findById(userDb.getId()))
                .expectNextMatches(newUserDb ->
                        newUserDb != null &&
                        newUserDb.getId().equals(user.getId()) &&
                        newUserDb.getUsername().equals(user.getUsername()) &&
                        newUserDb.getPassword() != null &&
                        newUserDb.getRole().equals(user.getRole()) &&
                        newUserDb.isEnabled()
                )
                .verifyComplete();
    }

    @Test
    void findByUsername() {
        User userDb = userRepository.save(user).block();

        assertNotNull(userDb);
        assertNotNull(userDb.getId());

        StepVerifier.create(userRepository.findByUsername(userDb.getUsername()))
                .expectNextMatches(newUserDb -> newUserDb != null &&
                        newUserDb.getId().equals(user.getId()) &&
                        newUserDb.getUsername().equals(user.getUsername()) &&
                        newUserDb.getPassword() != null &&
                        newUserDb.getRole().equals(user.getRole()) &&
                        newUserDb.isEnabled()
                )
                .verifyComplete();
    }

    @Test
    void findAll() {
        User userDb = userRepository.save(user).block();

        assertNotNull(userDb);
        assertNotNull(userDb.getId());

        StepVerifier.create(userRepository.findAll().collectList())
                .expectNextMatches(users -> users != null && users.size() == 1)
                .verifyComplete();
    }

    @Test
    void save() {
        User userDb = userRepository.save(user).block();

        assertNotNull(userDb);
        assertNotNull(userDb.getId());
    }

    @Test
    void deleteById() {
        User userDb = userRepository.save(user).block();

        assertNotNull(userDb);
        assertNotNull(userDb.getId());

        StepVerifier.create(userRepository.deleteById(userDb.getId()).then(userRepository.findById(userDb.getId())))
                .verifyComplete();
    }
}