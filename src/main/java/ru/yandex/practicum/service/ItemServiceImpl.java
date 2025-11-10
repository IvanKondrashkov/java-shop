package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.dto.ItemInfo;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.mapper.ItemMapper;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.exception.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(readOnly = true)
    public ItemInfo findById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item not found!")
        );
        return ItemMapper.itemToItemInfo(item, cartItemRepository.countByItem_Id(item.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemInfo> findAll(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(it -> ItemMapper.itemToItemInfo(it, cartItemRepository.countByItem_Id(it.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemInfo> findAllBySearch(String search, Pageable pageable) {
        return itemRepository.findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable)
                .map(it -> ItemMapper.itemToItemInfo(it, cartItemRepository.countByItem_Id(it.getId())));
    }
}