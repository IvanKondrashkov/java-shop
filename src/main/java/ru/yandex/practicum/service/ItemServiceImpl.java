package ru.yandex.practicum.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.exception.EntityNotFoundException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemInfo> findById(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Item not found!")))
                .flatMap(it -> Mono.zip(
                        Mono.just(it),
                        imageRepository.findById(it.getImageId()),
                        cartItemRepository.countByItemId(it.getId()).defaultIfEmpty(0))
                )
                .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemInfo> findAll(Integer limit, Integer offset, String sort) {
        return itemRepository.findAll(limit, offset, sort)
                .flatMap(it -> Mono.zip(
                        Mono.just(it),
                        imageRepository.findById(it.getImageId()),
                        cartItemRepository.countByItemId(it.getId()).defaultIfEmpty(0))
                )
                .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemInfo> findAllBySearch(String search, Integer limit, Integer offset, String sort) {
        return itemRepository.findAllBySearch(search, search, limit, offset, sort)
                .flatMap(it -> Mono.zip(
                        Mono.just(it),
                        imageRepository.findById(it.getImageId()),
                        cartItemRepository.countByItemId(it.getId()).defaultIfEmpty(0))
                )
                .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> count() {
        return itemRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countBySearch(String search) {
        return itemRepository.countBySearch(search, search);
    }
}