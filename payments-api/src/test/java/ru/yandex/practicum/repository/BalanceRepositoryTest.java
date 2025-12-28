package ru.yandex.practicum.repository;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import ru.yandex.practicum.dto.Role;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.Balance;
import static org.junit.jupiter.api.Assertions.*;

public class BalanceRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    private User user;
    private Balance balance;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("Djon")
                .password("123456")
                .role(Role.USER.name())
                .enabled(true)
                .build();
        balance = Balance.builder()
                .currency("RUB")
                .balance(BigDecimal.ZERO)
                .build();

        userRepository.save(user)
                .doOnNext(newUser -> balance.setUserId(newUser.getId()))
                .block();
    }

    @AfterEach
    void tearDown() {
        user = null;
        balance = null;

        balanceRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    void findById() {
        Balance balanceDb = balanceRepository.save(balance).block();

        assertNotNull(balanceDb);
        assertNotNull(balanceDb.getId());

        StepVerifier.create(balanceRepository.findById(balanceDb.getId()))
                .expectNextMatches(newBalanceDb ->
                        newBalanceDb != null &&
                        newBalanceDb.getId() != null &&
                        newBalanceDb.getCurrency().equals(balance.getCurrency()) &&
                        newBalanceDb.getBalance().compareTo(balance.getBalance()) == 0 &&
                        newBalanceDb.getUserId().equals(user.getId())
                )
                .verifyComplete();
    }

    @Test
    void findByUserId() {
        Balance balanceDb = balanceRepository.save(balance).block();

        assertNotNull(balanceDb);
        assertNotNull(balanceDb.getId());

        StepVerifier.create(balanceRepository.findByUserId(balanceDb.getUserId()))
                .expectNextMatches(newBalanceDb ->
                        newBalanceDb != null &&
                        newBalanceDb.getId() != null &&
                        newBalanceDb.getCurrency().equals(balance.getCurrency()) &&
                        newBalanceDb.getBalance().compareTo(balance.getBalance()) == 0 &&
                        newBalanceDb.getUserId().equals(user.getId())
                )
                .verifyComplete();
    }

    @Test
    void findAll() {
        Balance balanceDb = balanceRepository.save(balance).block();

        assertNotNull(balanceDb);
        assertNotNull(balanceDb.getId());

        StepVerifier.create(balanceRepository.findAll().collectList())
                .expectNextMatches(balances -> balances != null && balances.size() == 1)
                .verifyComplete();
    }

    @Test
    void save() {
        Balance balanceDb = balanceRepository.save(balance).block();

        assertNotNull(balanceDb);
        assertNotNull(balanceDb.getId());
    }

    @Test
    void deleteById() {
        Balance balanceDb = balanceRepository.save(balance).block();

        assertNotNull(balanceDb);
        assertNotNull(balanceDb.getId());

        StepVerifier.create(balanceRepository.deleteById(balanceDb.getId()).then(balanceRepository.findById(balanceDb.getId())))
                .verifyComplete();
    }
}