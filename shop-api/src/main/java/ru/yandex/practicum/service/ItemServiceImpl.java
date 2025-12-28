package ru.yandex.practicum.service;

import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.repository.ImageRepository;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.exception.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final CacheService cacheService;

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemInfo> findById(Long id) {
        return userService.getCurrentUserId()
                .flatMap(userId -> processItem(id)
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Item not found!")))
                        .flatMap(item -> Mono.zip(
                                Mono.just(item),
                                processImage(item.getId()),
                                cartItemRepository.findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), userId)
                                        .map(CartItem::getQuantity)
                                        .defaultIfEmpty(0))
                        )
                        .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemInfo> findAll(Integer limit, Integer offset, String sort) {
        return userService.getCurrentUserId()
                .flatMapMany(userId -> processItems(limit, offset, sort)
                        .switchIfEmpty(itemRepository.findAll(limit, offset, sort))
                        .flatMap(item -> Mono.zip(
                                Mono.just(item),
                                processImage(item.getId()),
                                cartItemRepository.findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), userId)
                                        .map(CartItem::getQuantity)
                                        .defaultIfEmpty(0))
                        )
                        .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                );

    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ItemInfo> findAllBySearch(String search, Integer limit, Integer offset, String sort) {
        return userService.getCurrentUserId()
                .flatMapMany(userId -> processItemsSearch(search, limit, offset, sort)
                        .flatMap(item -> Mono.zip(
                                Mono.just(item),
                                processImage(item.getId()),
                                cartItemRepository.findByItemIdAndUserIdAndOrderIdIsNull(item.getId(), userId)
                                        .map(CartItem::getQuantity)
                                        .defaultIfEmpty(0))
                        )
                        .map(tuple -> ItemMapper.itemToItemInfo(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                );
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
        return cacheService.get("item", id.toString(), Item.class)
                .switchIfEmpty(itemRepository.findById(id))
                .flatMap(item -> cacheService.save("item", item.getId().toString(), item, Duration.ofMinutes(10)).thenReturn(item));
    }

    private Mono<Image> processImage(Long itemId) {
        return cacheService.get("image", itemId.toString(), Image.class)
                .switchIfEmpty(imageRepository.findByItemId(itemId))
                .flatMap(image -> cacheService.save("image", itemId.toString(), image, Duration.ofMinutes(10)).thenReturn(image));
    }

    private Flux<Item> processItems(Integer limit, Integer offset, String sort) {
        final String cacheKey = String.format("items:list:%d:%d:%s", limit, offset, sort);
        return cacheService.getList(cacheKey, Item.class)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(
                        itemRepository.findAll(limit, offset, sort)
                                .collectList()
                                .flatMap(items -> cacheService.saveList(cacheKey, items, Duration.ofMinutes(10)).thenReturn(items))
                                .flatMapMany(Flux::fromIterable)
                );
    }

    private Flux<Item> processItemsSearch(String search, Integer limit, Integer offset, String sort) {
        final String cacheKey = String.format("items:search:%d:%d:%s", limit, offset, sort);
        return cacheService.getList(cacheKey, Item.class)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(
                        itemRepository.findAllBySearch(search, search, limit, offset, sort)
                                .collectList()
                                .flatMap(items -> cacheService.saveList(cacheKey, items, Duration.ofMinutes(5)).thenReturn(items))
                                .flatMapMany(Flux::fromIterable)
                );
    }
}