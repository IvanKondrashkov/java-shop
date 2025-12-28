package ru.yandex.practicum.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import ru.yandex.practicum.dto.Role;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.Payment;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    private User user;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("Djon")
                .password("123456")
                .role(Role.USER.name())
                .enabled(true)
                .build();
        payment = Payment.builder()
                .currency("RUB")
                .amount(BigDecimal.valueOf(14999.00))
                .status("SUCCESS")
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user)
                .doOnNext(newUser -> payment.setUserId(newUser.getId()))
                .block();
    }

    @AfterEach
    void tearDown() {
        user = null;
        payment = null;

        paymentRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    void findById() {
        Payment paymentDb = paymentRepository.save(payment).block();

        assertNotNull(paymentDb);
        assertNotNull(paymentDb.getId());

        StepVerifier.create(paymentRepository.findById(paymentDb.getId()))
                .expectNextMatches(newPaymentDb ->
                        newPaymentDb != null &&
                        newPaymentDb.getId() != null &&
                        newPaymentDb.getCurrency().equals(payment.getCurrency()) &&
                        newPaymentDb.getAmount().compareTo(payment.getAmount()) == 0 &&
                        newPaymentDb.getStatus().equals("SUCCESS") &&
                        newPaymentDb.getCreatedAt() != null &&
                        newPaymentDb.getUserId().equals(user.getId())
                )
                .verifyComplete();
    }

    @Test
    void findAll() {
        Payment paymentDb = paymentRepository.save(payment).block();

        assertNotNull(paymentDb);
        assertNotNull(paymentDb.getId());

        StepVerifier.create(paymentRepository.findAll().collectList())
                .expectNextMatches(payments -> payments != null && payments.size() == 1)
                .verifyComplete();
    }

    @Test
    void save() {
        Payment paymentDb = paymentRepository.save(payment).block();

        assertNotNull(paymentDb);
        assertNotNull(paymentDb.getId());
    }

    @Test
    void deleteById() {
        Payment paymentDb = paymentRepository.save(payment).block();

        assertNotNull(paymentDb);
        assertNotNull(paymentDb.getId());

        StepVerifier.create(paymentRepository.deleteById(paymentDb.getId()).then(paymentRepository.findById(paymentDb.getId())))
                .verifyComplete();
    }
}