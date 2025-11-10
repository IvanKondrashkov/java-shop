package ru.yandex.practicum.controller;

import java.util.List;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.dto.request.UserRequest;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.dto.request.ActionRequest;
import ru.yandex.practicum.service.CartItemService;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.stereotype.Controller;
import ru.yandex.practicum.utils.RenderingUtils;

@Slf4j
@Controller
@RequestMapping("cart/items")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;
    private final PaymentClient paymentClient;

    @GetMapping
    public Mono<Rendering> findAll(@ModelAttribute UserRequest userRequest) {
        return cartItemService.findAll()
                .collectList()
                .flatMap(items -> {
                    BigDecimal total = items.stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return paymentClient.getBalance(userRequest.getUserId())
                            .map(balance -> balance.getBalance().compareTo(total) >= 0)
                            .defaultIfEmpty(false)
                            .flatMap(isSufficient ->
                                    RenderingUtils.renderCart(items, total, isSufficient)
                            );
                });
    }

    @PostMapping
    public Mono<Rendering> purchaseItem(@ModelAttribute UserRequest userRequest, @ModelAttribute ActionRequest actionRequest) {
        return getAction(actionRequest.getId(), actionRequest.getAction())
                .flatMap(items -> {
                    BigDecimal total = items.stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return paymentClient.getBalance(userRequest.getUserId())
                            .map(balance -> balance.getBalance().compareTo(total) >= 0)
                            .defaultIfEmpty(false)
                            .flatMap(isSufficient ->
                                    RenderingUtils.renderCart(items, total, isSufficient)
                            );
                });
    }

    private Mono<List<ItemInfo>> getAction(Long id, Action action) {
        switch (action) {
            case PLUS, MINUS -> {
                return cartItemService.purchaseItem(id, action)
                        .then(cartItemService.findAll().collectList());
            }
            case DELETE -> {
                return cartItemService.deleteById(id, action)
                        .then(cartItemService.findAll().collectList());
            }
            default -> {
                return cartItemService.findAll().collectList();
            }
        }
    }
}