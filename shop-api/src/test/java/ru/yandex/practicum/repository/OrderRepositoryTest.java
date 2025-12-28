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
import ru.yandex.practicum.model.Order;
import static org.junit.jupiter.api.Assertions.*;

public class OrderRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    private User user;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("Djon")
                .password("123456")
                .role(Role.USER.name())
                .enabled(true)
                .build();
        order = Order.builder()
                .totalSum(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user)
                .doOnNext(newUser -> order.setUserId(newUser.getId()))
                .block();
    }

    @AfterEach
    void tearDown() {
        user = null;
        order = null;

        orderRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    void findById() {
        Order orderDb = orderRepository.save(order).block();

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());

        StepVerifier.create(orderRepository.findById(orderDb.getId()))
                .expectNextMatches(newOrderDb ->
                        newOrderDb != null &&
                        newOrderDb.getId() != null &&
                        newOrderDb.getTotalSum().compareTo(BigDecimal.ZERO) == 0 &&
                        newOrderDb.getCreatedAt() != null
                )
                .verifyComplete();
    }

    @Test
    void findByIdAndUserId() {
        Order orderDb = orderRepository.save(order).block();

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());

        StepVerifier.create(orderRepository.findByIdAndUserId(orderDb.getId(), orderDb.getUserId()))
                .expectNextMatches(newOrderDb ->
                        newOrderDb != null &&
                        newOrderDb.getId() != null &&
                        newOrderDb.getTotalSum().compareTo(BigDecimal.ZERO) == 0 &&
                        newOrderDb.getCreatedAt() != null
                )
                .verifyComplete();
    }

    @Test
    void findAllByUserId() {
        Order orderDb = orderRepository.save(order).block();

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());

        StepVerifier.create(orderRepository.findAllByUserId(orderDb.getUserId()))
                .expectNextMatches(newOrderDb ->
                        newOrderDb != null &&
                        newOrderDb.getId() != null &&
                        newOrderDb.getTotalSum().compareTo(BigDecimal.ZERO) == 0 &&
                        newOrderDb.getCreatedAt() != null
                )
                .verifyComplete();
    }

    @Test
    void findAll() {
        Order orderDb = orderRepository.save(order).block();

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());

        StepVerifier.create(orderRepository.findAll().collectList())
                .expectNextMatches(orders -> orders != null && orders.size() == 1)
                .verifyComplete();
    }

    @Test
    void save() {
        Order orderDb = orderRepository.save(order).block();

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());
    }

    @Test
    void deleteById() {
        Order orderDb = orderRepository.save(order).block();

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());

        StepVerifier.create(orderRepository.deleteById(orderDb.getId()).then(orderRepository.findById(orderDb.getId())))
                .verifyComplete();
    }
}