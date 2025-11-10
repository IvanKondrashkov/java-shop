package ru.yandex.practicum.controller;

import java.util.List;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.dto.ItemInfo;
import ru.yandex.practicum.service.CartItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("cart/items")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping
    public String findAll(Model model) {
        List<ItemInfo> items = cartItemService.findAll();
        BigDecimal total = BigDecimal.valueOf(items.stream()
                .mapToDouble(it -> it.getPrice().doubleValue() * it.getCount())
                .sum());

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "cart";
    }

    @PostMapping
    public String purchaseItem(@RequestParam Long id, @RequestParam Action action, Model model) {
        switch (action) {
            case PLUS, MINUS -> cartItemService.purchaseItem(id, action);
            case DELETE -> cartItemService.deleteById(id, action);
        }

        List<ItemInfo> items = cartItemService.findAll();
        BigDecimal total = BigDecimal.valueOf(items.stream()
                .mapToDouble(it -> it.getPrice().doubleValue() * it.getCount())
                .sum());

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        return "cart";
    }
}