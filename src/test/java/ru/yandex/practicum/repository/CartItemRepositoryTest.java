package ru.yandex.practicum.repository;

import java.util.List;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
                .item(item)
                .build();

        itemRepository.save(item);

    }

    @AfterEach
    void tearDown() {
        item = null;
        cartItem = null;

        cartItemRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findById() {
        CartItem cartItemDb = cartItemRepository.save(cartItem);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        cartItemDb = cartItemRepository.findById(cartItemDb.getId()).orElse(null);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());
    }

    @Test
    void findByItemId() {
        CartItem cartItemDb = cartItemRepository.save(cartItem);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        cartItemDb = cartItemRepository.findByItem_Id(cartItemDb.getItem().getId()).orElse(null);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());
    }

    @Test
    void findAll() {
        CartItem cartItemDb = cartItemRepository.save(cartItem);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        List<CartItem> cartItems = cartItemRepository.findAll();

        assertNotNull(cartItems);
        assertEquals(cartItems.size(), 1);
    }

    @Test
    void findAllByOrderIsNull() {
        CartItem cartItemDb = cartItemRepository.save(cartItem);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        List<CartItem> cartItems = cartItemRepository.findAllByOrderIsNull();

        assertNotNull(cartItems);
        assertEquals(cartItems.size(), 1);
    }

    @Test
    void save() {
        CartItem cartItemDb = cartItemRepository.save(cartItem);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());
    }

    @Test
    void deleteById() {
        CartItem cartItemDb = cartItemRepository.save(cartItem);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        cartItemRepository.deleteById(cartItemDb.getId());
        cartItemDb = cartItemRepository.findById(cartItemDb.getId()).orElse(null);

        assertNull(cartItemDb);
    }

    @Test
    @Transactional
    void deleteByItemId() {
        CartItem cartItemDb = cartItemRepository.save(cartItem);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        cartItemRepository.deleteByItem_Id(cartItemDb.getItem().getId());
        cartItemDb = cartItemRepository.findById(cartItemDb.getId()).orElse(null);

        assertNull(cartItemDb);
    }

    @Test
    void countAllByItemId() {
        CartItem cartItemDb = cartItemRepository.save(cartItem);

        assertNotNull(cartItemDb);
        assertNotNull(cartItemDb.getId());

        int count = cartItemRepository.countByItem_Id(cartItemDb.getItem().getId());

        assertEquals(count, 1);
    }
}