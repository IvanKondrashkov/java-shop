package ru.yandex.practicum.controller;

import java.util.List;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.dto.request.ActionRequest;
import ru.yandex.practicum.service.CartItemService;
import org.springframework.web.reactive.result.view.Rendering;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.utils.RenderingUtils;

@Slf4j
@Controller
@RequestMapping("cart/items")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;
    private final PaymentClient paymentClient;
    private final UserService userService;

    @Secured("ROLE_USER")
    @GetMapping
    public Mono<Rendering> findAll() {
        return cartItemService.findAll()
                .collectList()
                .flatMap(items -> {
                    BigDecimal total = items.stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return userService.getCurrentUserId()
                            .flatMap(userId -> paymentClient.getBalance(userId)
                                    .map(balance -> balance.getBalance().compareTo(total) >= 0)
                                    .defaultIfEmpty(false)
                                    .flatMap(isSufficient ->
                                            RenderingUtils.renderCart(items, total, isSufficient)
                                    )
                            );
                });
    }

    @Secured("ROLE_USER")
    @PostMapping
    public Mono<Rendering> purchaseItem(@ModelAttribute ActionRequest actionRequest) {
        return getAction(actionRequest.getId(), actionRequest.getAction())
                .flatMap(items -> {
                    BigDecimal total = items.stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return userService.getCurrentUserId()
                            .flatMap(userId -> paymentClient.getBalance(userId)
                                    .map(balance -> balance.getBalance().compareTo(total) >= 0)
                                    .defaultIfEmpty(false)
                                    .flatMap(isSufficient ->
                                            RenderingUtils.renderCart(items, total, isSufficient)
                                    )
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