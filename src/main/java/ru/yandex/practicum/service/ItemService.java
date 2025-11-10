package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.ItemInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {
    ItemInfo findById(Long id);
    Page<ItemInfo> findAll(Pageable pageable);
    Page<ItemInfo> findAllBySearch(String search, Pageable pageable);
}