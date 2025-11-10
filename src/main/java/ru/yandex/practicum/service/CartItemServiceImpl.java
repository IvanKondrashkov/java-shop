package ru.yandex.practicum.service;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.dto.ItemInfo;
import ru.yandex.practicum.dto.OrderInfo;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.CartItem;
import ru.yandex.practicum.mapper.ItemMapper;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.repository.CartItemRepository;
import ru.yandex.practicum.exception.EntityIsEmptyException;
import ru.yandex.practicum.exception.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemInfo> findAll() {
        return cartItemRepository.findAllByOrderIsNull().stream()
                .map(CartItem::getItem)
                .map(it -> ItemMapper.itemToItemInfo(it, cartItemRepository.countByItem_Id(it.getId())))
                .toList();
    }

    @Override
    public ItemInfo purchaseItem(Long id, Action action) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item not found!")
        );

        CartItem cartItem = cartItemRepository.findByItem_Id(item.getId()).orElse(
                CartItem.builder()
                        .quantity(0)
                        .item(item)
                        .build()
        );

        int newQuantity = cartItem.getQuantity();
        switch (action) {
            case PLUS -> newQuantity++;
            case MINUS -> {
                if (newQuantity >= 0) {
                    newQuantity--;
                }
            }
        }
        cartItem.setQuantity(newQuantity);

        CartItem cartItemDb = cartItemRepository.save(cartItem);
        return ItemMapper.itemToItemInfo(cartItemDb.getItem(), cartItemDb.getQuantity());
    }

    @Override
    public OrderInfo purchaseOrder() {
        Set<CartItem> cartItems = new HashSet<>(cartItemRepository.findAllByOrderIsNull());

        if (cartItems.isEmpty()) {
            throw new EntityIsEmptyException("Cart is empty!");
        }

        Order order = Order.builder()
                .totalSum(BigDecimal.valueOf(cartItems.stream()
                        .mapToDouble(it -> it.getItem().getPrice().doubleValue() * it.getQuantity())
                        .sum()))
                .createdAt(LocalDateTime.now())
                .items(cartItems)
                .build();

        cartItems.forEach(cartItem -> cartItem.setOrder(order));
        Order orderDb = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);
        return OrderMapper.orderToOrderInfo(orderDb);
    }

    @Override
    public void deleteById(Long id, Action action) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Item not found!")
        );

        CartItem cartItem = cartItemRepository.findByItem_Id(item.getId()).orElseThrow(
                () -> new EntityNotFoundException("Cart item not found!")
        );

        if (action == Action.DELETE) {
            cartItemRepository.deleteByItem_Id(cartItem.getItem().getId());
        }
    }
}