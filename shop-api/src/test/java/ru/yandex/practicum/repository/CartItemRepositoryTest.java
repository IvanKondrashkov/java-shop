package ru.yandex.practicum.repository;

import java.util.List;
import java.util.Random;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;
import ru.yandex.practicum.dto.Role;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.CartItem;
import static org.junit.jupiter.api.Assertions.*;

public class CartItemRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    private Item item;
    private User user;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .title("Внешний SSD Samsung T7")
                .description("Portable SSD 1ТБ со скоростью передачи до 1050 МБ/с")
                .price(BigDecimal.valueOf(14999.00))
                .build();
        user = User.builder()
                .username("Djon")
                .password("123456")
                .role(Role.USER.name())
                .enabled(true)
                .build();
        cartItem = CartItem.builder()
                .quantity(1)
                .build();

        itemRepository.save(item)
                .doOnNext(newItem -> cartItem.setItemId(newItem.getId()))
                .block();

        userRepository.save(user)
                .doOnNext(newUser -> cartItem.setUserId(newUser.getId()))
                .block();

    }

    @AfterEach
    void tearDown() {
        item = null;
        user = null;
        cartItem = null;

        cartItemRepository.deleteAll().block();
        userRepository.deleteAll().block();
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
    void findByItemIdAndUserIdAndOrderIdIsNull() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.findByItemIdAndUserIdAndOrderIdIsNull(cartItemDb.getItemId(), cartItemDb.getUserId()))
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
    void findAllByOrderIdAndUserId() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.findAllByOrderIdAndUserId(cartItemDb.getOrderId(), cartItemDb.getUserId()).collectList())
                .expectNextMatches(cartItems -> cartItems != null && cartItems.size() == 1)
                .verifyComplete();
    }

    @Test
    void findAllByUserIdAndOrderIdIsNull() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.findAllByUserIdAndOrderIdIsNull(new Random().nextLong()).collectList())
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

        StepVerifier.create(cartItemRepository.deleteById(cartItemDb.getId()).then(cartItemRepository.findById(cartItemDb.getId())))
                .verifyComplete();
    }

    @Test
    void deleteByItemIdAndUserId() {
        CartItem cartItemDb = cartItemRepository.save(cartItem).block();

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        StepVerifier.create(cartItemRepository.deleteByItemIdAndUserId(cartItemDb.getItemId(), cartItemDb.getUserId()).then(cartItemRepository.findById(cartItemDb.getId())))
                .verifyComplete();
    }
}