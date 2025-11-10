package ru.yandex.practicum.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import ru.yandex.practicum.dto.*;
import org.springframework.ui.Model;
import ru.yandex.practicum.service.OrderService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("orders/{id}")
    public String findById(@PathVariable Long id, @RequestParam(defaultValue = "false") Boolean newOrder, Model model) {
        OrderInfo order = orderService.findById(id);

        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }

    @GetMapping("orders")
    public String findAll(Model model) {
        List<OrderInfo> orders = orderService.findAll();

        model.addAttribute("orders", orders);
        return "orders";
    }

    @PostMapping ("buy")
    public String buy() {
        OrderInfo order = orderService.buy();
        return String.format("redirect:/orders/%d?newOrder=true", order.getId());
    }
}