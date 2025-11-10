package ru.yandex.practicum.service;

import java.util.Optional;
import java.math.BigDecimal;
import java.util.Collections;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.dto.ItemInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.CartItemRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;


    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .title("Внешний SSD Samsung T7")
                .description("Portable SSD 1ТБ со скоростью передачи до 1050 МБ/с")
                .price(BigDecimal.valueOf(14999.00))
                .build();
    }

    @AfterEach
    void tearDown() {
        item = null;
    }

    @Test
    void findById() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(cartItemRepository.countByItem_Id(item.getId())).thenReturn(1);

        ItemInfo itemDb = itemService.findById(item.getId());

        assertNotNull(itemDb);
        assertEquals(itemDb.getTitle(), item.getTitle());
        assertEquals(itemDb.getDescription(), item.getDescription());
        assertEquals(itemDb.getPrice(), item.getPrice());
        assertEquals(itemDb.getCount(), 1);

        verify(itemRepository, times(1)).findById(item.getId());
        verify(cartItemRepository, times(1)).countByItem_Id(item.getId());
    }

    @Test
    void findAll() {
        PageRequest pageRequest = PageRequest.of(1, 5);

        when(itemRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(Collections.singletonList(item)));
        when(cartItemRepository.countByItem_Id(item.getId())).thenReturn(1);

        Page<ItemInfo> page = itemService.findAll(pageRequest);

        assertNotNull(page.getContent());
        assertEquals(page.getContent().size(), 1);

        verify(itemRepository, times(1)).findAll(pageRequest);
        verify(cartItemRepository, times(1)).countByItem_Id(item.getId());
    }

    @Test
    void findAllBySearch() {
        PageRequest pageRequest = PageRequest.of(1, 5);

        when(itemRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("SSD", "SSD", pageRequest)).thenReturn(new PageImpl<>(Collections.singletonList(item)));
        when(cartItemRepository.countByItem_Id(item.getId())).thenReturn(1);

        Page<ItemInfo> page = itemService.findAllBySearch("SSD", pageRequest);

        assertNotNull(page.getContent());
        assertEquals(page.getContent().size(), 1);

        verify(itemRepository, times(1)).findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase("SSD", "SSD", pageRequest);
        verify(cartItemRepository, times(1)).countByItem_Id(item.getId());
    }
}