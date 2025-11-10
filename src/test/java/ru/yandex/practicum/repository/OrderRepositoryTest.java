package ru.yandex.practicum.repository;

import java.util.Set;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.model.Order;
import static org.junit.jupiter.api.Assertions.*;

public class OrderRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .totalSum(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .items(Set.of())
                .build();
    }

    @AfterEach
    void tearDown() {
        order = null;

        orderRepository.deleteAll();
    }

    @Test
    void findById() {
        Order orderDb = orderRepository.save(order);

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());

        orderDb = orderRepository.findById(orderDb.getId()).orElse(null);

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());
    }

    @Test
    void findAll() {
        Order orderDb = orderRepository.save(order);

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());

        List<Order> orders = orderRepository.findAll();

        assertNotNull(orders);
        assertEquals(orders.size(), 1);
    }

    @Test
    void save() {
        Order orderDb = orderRepository.save(order);

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());
    }

    @Test
    void deleteById() {
        Order orderDb = orderRepository.save(order);

        assertNotNull(orderDb);
        assertNotNull(orderDb.getId());

        orderRepository.deleteById(orderDb.getId());
        orderDb = orderRepository.findById(orderDb.getId()).orElse(null);

        assertNull(orderDb);
    }
}