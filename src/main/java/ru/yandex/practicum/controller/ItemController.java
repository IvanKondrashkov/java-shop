package ru.yandex.practicum.controller;

import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ru.yandex.practicum.dto.Sort;
import ru.yandex.practicum.dto.Order;
import ru.yandex.practicum.dto.Action;
import ru.yandex.practicum.dto.ItemInfo;
import ru.yandex.practicum.service.ItemService;
import ru.yandex.practicum.service.AdminService;
import ru.yandex.practicum.service.CartItemService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import static org.springframework.data.domain.Sort.by;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CartItemService cartItemService;
    private final AdminService adminService;

    @GetMapping("/")
    public String redirectToItems() {
        return "redirect:/items";
    }

    @GetMapping("items/{id}")
    public String findById(@PathVariable Long id, Model model) {
        ItemInfo itemInfo = itemService.findById(id);
        model.addAttribute("item", itemInfo);
        return "item";
    }

    @GetMapping("items")
    public String findAll(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "NO") Sort sort,
            @RequestParam(defaultValue = "DESC") Order order,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "5") Integer pageSize,
            Model model)
    {

        PageRequest pageRequest = sort == Sort.NO ?
                PageRequest.of(pageNumber, pageSize) :
                PageRequest.of(pageNumber, pageSize, by(order.name(), sort.getValue()));

        Page<ItemInfo> paging = search.isEmpty() ?
                itemService.findAll(pageRequest) :
                itemService.findAllBySearch(search, pageRequest);

        List<List<ItemInfo>> items = Stream.iterate(0, i -> i < paging.getContent().size(), i -> i + 3)
                .map(i -> paging.getContent().subList(i, Math.min(i + 3, paging.getContent().size())))
                .toList();

        model.addAttribute("sort", sort);
        model.addAttribute("order", order);
        model.addAttribute("items", items);
        model.addAttribute("paging", paging);
        model.addAttribute("search", search);
        return "items";
    }

    @PostMapping("items/{id}")
    public String purchaseItemById(@PathVariable Long id, @RequestParam Action action, Model model) {
        ItemInfo itemInfo = cartItemService.purchaseItem(id, action);
        model.addAttribute("item", itemInfo);
        return "item";
    }

    @PostMapping("items")
    public String purchaseItem(
            @RequestParam Long id,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "NO") Sort sort,
            @RequestParam(defaultValue = "DESC") Order order,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam Action action)
    {
        cartItemService.purchaseItem(id, action);
        return String.format("redirect:/items?search=%s&sort=%s&order=%s&pageNumber=%d&pageSize=%d",
                search, sort.name(), order.name(), pageNumber, pageSize);
    }

    @PostMapping(value = "items/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importCsvFile(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "NO") Sort sort,
            @RequestParam(defaultValue = "DESC") Order order,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam MultipartFile file)
    {
        adminService.importCsvFile(file);
        return String.format("redirect:/items?search=%s&sort=%s&order=%s&pageNumber=%d&pageSize=%d",
                search, sort.name(), order.name(), pageNumber, pageSize);
    }
}