package ru.yandex.practicum.repository;

import java.util.List;
import java.util.Random;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.CartItem;
import static org.junit.jupiter.api.Assertions.*;

public class CartItemRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    private Item item;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .title("Внешний SSD Samsung T7")
                .description("Portable SSD 1ТБ со скоростью передачи до 1050 МБ/с")
                .price(BigDecimal.valueOf(14999.00))
                .build();
        cartItem = CartItem.builder()
                .quantity(1)
                .build();

        itemRepository.save(item)
                .doOnNext(newItem -> cartItem.setItemId(newItem.getId()))
                .block();

    }

    @AfterEach
    void tearDown() {
        item = null;
        cartItem = null;

        cartItemRepository.deleteAll().block();
        itemRepository.deleteAll().block();
    }

    @Test
    void findById() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.findById(cartItemDb.getId()))
                .expectNextMatches(newCartItemDb ->
                        newCartItemDb != null &&
                        newCartItemDb.getId() != null &&
                        newCartItemDb.getQuantity().equals(1)
                )
                .verifyComplete();
    }

    @Test
    void findByItemId() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.findByItemIdAndOrderIdIsNull(cartItemDb.getItemId()))
                .expectNextMatches(newCartItemDb ->
                        newCartItemDb != null &&
                        newCartItemDb.getId() != null &&
                        newCartItemDb.getQuantity().equals(1)
                )
                .verifyComplete();
    }

    @Test
    void findAll() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.findAll().collectList())
                .expectNextMatches(cartItems -> cartItems != null && cartItems.size() == 1)
                .verifyComplete();
    }

    @Test
    void findAllByOrderIsNull() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.findAllByOrderIdIsNull().collectList())
                .expectNextMatches(cartItems -> cartItems != null && cartItems.size() == 1)
                .verifyComplete();
    }

    @Test
    void findAllByOrderId() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.findAllByOrderId(new Random().nextLong()).collectList())
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void save() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());
    }

    @Test
    void deleteById() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.deleteById(cartItemDb.getId())
                        .then(cartItemRepository.findById(cartItemDb.getId()))
                )
                .verifyComplete();
    }

    @Test
    void deleteByItemId() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.deleteByItemId(cartItemDb.getItemId())
                        .then(cartItemRepository.findById(cartItemDb.getId()))
                )
                .verifyComplete();
    }

    @Test
    void countAllByItemId() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.countByItemId(cartItemDb.getItemId()))
                .expectNext(1)
                .verifyComplete();
    }
}