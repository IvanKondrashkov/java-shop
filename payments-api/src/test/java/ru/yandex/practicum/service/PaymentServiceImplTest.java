package ru.yandex.practicum.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.Balance;
import ru.yandex.practicum.repository.UserRepository;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.repository.BalanceRepository;
import ru.yandex.practicum.server.model.PaymentRequest;
import ru.yandex.practicum.server.model.PaymentResponse;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @InjectMocks
    private PaymentServiceImpl paymentService;
    private User user;
    private Balance balance;
    private Payment payment;
    private PaymentRequest paymentRequest;


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
                .balance(BigDecimal.valueOf(15000.00))
                .userId(user.getId())
                .build();
        payment = Payment.builder()
                .id(1L)
                .currency("RUB")
                .amount(BigDecimal.valueOf(14999.00))
                .status("SUCCESS")
                .createdAt(LocalDateTime.now())
                .userId(user.getId())
                .build();

        paymentRequest = new PaymentRequest();
        paymentRequest.setUserId(user.getId());
        paymentRequest.setAmount(BigDecimal.valueOf(14999.00));
    }

    @AfterEach
    void tearDown() {
        user = null;
        balance = null;
        payment = null;
        paymentRequest = null;
    }

    @Test
    void processPayment() {
        when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        when(balanceRepository.findByUserId(user.getId())).thenReturn(Mono.just(balance));
        when(balanceRepository.save(any(Balance.class))).thenReturn(Mono.just(balance));
        when(paymentRepository.save(any(Payment.class))).thenReturn(Mono.just(payment));

        StepVerifier.create(paymentService.processPayment(paymentRequest))
                .expectNextMatches(newPayment ->
                        newPayment != null &&
                        newPayment.getCurrency().equals(balance.getCurrency()) &&
                        newPayment.getStatus() == PaymentResponse.StatusEnum.SUCCESS
                )
                .verifyComplete();

        verify(userRepository, times(1)).findById(user.getId());
        verify(balanceRepository, times(1)).findByUserId(user.getId());
        verify(balanceRepository, times(1)).save(any(Balance.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}