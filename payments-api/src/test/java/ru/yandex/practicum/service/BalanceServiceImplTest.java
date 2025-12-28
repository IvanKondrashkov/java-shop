package ru.yandex.practicum.service;

import org.mockito.Mock;
import java.math.BigDecimal;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.Balance;
import ru.yandex.practicum.repository.BalanceRepository;
import ru.yandex.practicum.repository.UserRepository;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BalanceRepository balanceRepository;
    @InjectMocks
    private BalanceServiceImpl balanceService;
    private User user;
    private Balance balance;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Djon")
                .lastName("Doe")
                .build();
        balance = Balance.builder()
                .id(1L)
                .currency("RUB")
                .balance(BigDecimal.ZERO)
                .userId(user.getId())
                .build();
    }

    @AfterEach
    void tearDown() {
        user = null;
        balance = null;
    }

    @Test
    void findByUserId() {
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(balanceRepository.findByUserId(user.getId())).thenReturn(Mono.just(balance));

        StepVerifier.create(balanceService.findByUserId(user.getId()))
                .expectNextMatches(newBalance ->
                        newBalance != null &&
                        newBalance.getCurrency().equals(balance.getCurrency()) &&
                        newBalance.getBalance().compareTo(balance.getBalance()) == 0 &&
                        newBalance.getUserId().equals(user.getId())
                )
                .verifyComplete();

        verify(userRepository, times(1)).findById(user.getId());
        verify(balanceRepository, times(1)).findByUserId(user.getId());
    }
}