package ru.yandex.practicum.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;
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
    private final CacheService cacheService;

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemInfo> findById(Long id) {
        return processItem(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Item not found!")))
                .flatMap(it -> Mono.zip(
                        Mono.just(it),
                        processImage(it.getId()),
                        cartItemRepository.countByItemId(it.getId()).defaultIfEmpty(0))
                )
                .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemInfo> findAll(Integer limit, Integer offset, String sort) {
        return processItems(limit, offset, sort)
                .switchIfEmpty(itemRepository.findAll(limit, offset, sort))
                .flatMap(it -> Mono.zip(
                        Mono.just(it),
                        processImage(it.getId()),
                        cartItemRepository.countByItemId(it.getId()).defaultIfEmpty(0))
                )
                .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemInfo> findAllBySearch(String search, Integer limit, Integer offset, String sort) {
        return processItemsSearch(search, limit, offset, sort)
                .flatMap(it -> Mono.zip(
                        Mono.just(it),
                        processImage(it.getId()),
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


    private Mono<Item> processItem(Long id) {
        return cacheService.getItem(id.toString())
                .switchIfEmpty(itemRepository.findById(id))
                .flatMap(item -> cacheService.addItem(item).thenReturn(item));
    }

    private Mono<Image> processImage(Long itemId) {
        return cacheService.getImage(itemId.toString())
                .switchIfEmpty(imageRepository.findByItemId(itemId))
                .flatMap(image -> cacheService.addImage(itemId.toString(), image).thenReturn(image));
    }

    private Flux<Item> processItems(Integer limit, Integer offset, String sort) {
        final String cacheKey = String.format("items:list:%d:%d:%s", limit, offset, sort);
        return cacheService.getItems(cacheKey)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(
                        itemRepository.findAll(limit, offset, sort)
                                .collectList()
                                .flatMap(items -> cacheService.addItems(cacheKey, items).thenReturn(items))
                                .flatMapMany(Flux::fromIterable)
                );
    }

    private Flux<Item> processItemsSearch(String search, Integer limit, Integer offset, String sort) {
        final String cacheKey = String.format("items:search:%d:%d:%s", limit, offset, sort);
        return cacheService.getItemsSearch(cacheKey)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(
                        itemRepository.findAllBySearch(search, search, limit, offset, sort)
                                .collectList()
                                .flatMap(items -> cacheService.addItemsSearch(cacheKey, items).thenReturn(items))
                                .flatMapMany(Flux::fromIterable)
                );
    }
}