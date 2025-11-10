package ru.yandex.practicum.controller;

import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.*;
import ru.yandex.practicum.dto.request.ActionRequest;
import ru.yandex.practicum.dto.request.PageRequest;
import ru.yandex.practicum.dto.response.ItemInfo;
import ru.yandex.practicum.service.ItemService;
import ru.yandex.practicum.service.AdminService;
import ru.yandex.practicum.service.CartItemService;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.utils.RenderingUtils;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CartItemService cartItemService;
    private final AdminService adminService;

    @GetMapping("/")
    public Mono<Rendering> redirectToItems() {
        return Mono.just(RenderingUtils.redirectTo("/items"));
    }

    @GetMapping("items/{id}")
    public Mono<Rendering> findById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(RenderingUtils::renderItem);
    }

    @GetMapping("items")
    public Mono<Rendering> findAll(@ModelAttribute PageRequest pageRequest) {
        String search = pageRequest.getSearch();
        Integer pageNumber = pageRequest.getPageNumber();
        Integer pageSize = pageRequest.getPageSize();
        Integer offset = (pageNumber - 1) * pageSize;
        SortType sortType = pageRequest.getSort();
        OrderType orderType = pageRequest.getOrder();

        String sort = sortType == SortType.NO ? SortType.ID.getValue() : sortType.getValue();

        var page = search.isEmpty() ?
                itemService.findAll(pageSize, offset, sort)
                        .collectList()
                        .zipWith(itemService.count()) :
                itemService.findAllBySearch(search, pageSize, offset, sort)
                        .collectList()
                        .zipWith(itemService.countBySearch(search));

        return page.map(tuple -> {
            Boolean hasNext = (offset + pageSize) < tuple.getT2();
            Boolean hasPrevious = pageNumber > 1;

            Page paging = new Page(pageNumber, pageSize, offset, hasNext, hasPrevious);
            List<List<ItemInfo>> items = Stream.iterate(0, i -> i < tuple.getT1().size(), i -> i + 3)
                    .map(i -> tuple.getT1().subList(i, Math.min(i + 3, tuple.getT1().size())))
                    .toList();

            return Rendering.view("items")
                    .modelAttribute("sort", sortType)
                    .modelAttribute("order", orderType)
                    .modelAttribute("items", items)
                    .modelAttribute("paging", paging)
                    .modelAttribute("search", search)
                    .build();
        });
    }

    @PostMapping("items/{id}")
    public Mono<Rendering> purchaseItemById(@PathVariable Long id, @ModelAttribute ActionRequest actionRequest) {
        return cartItemService.purchaseItem(id, actionRequest.getAction())
                .map(RenderingUtils::renderItem);
    }

    @PostMapping("items")
    public Mono<Rendering> purchaseItem(@ModelAttribute ActionRequest actionRequest, @ModelAttribute PageRequest pageRequest) {
        return cartItemService.purchaseItem(actionRequest.getId(), actionRequest.getAction())
                .thenReturn(String.format("/items?search=%s&sort=%s&order=%s&pageNumber=%d&pageSize=%d",
                        pageRequest.getSearch(), pageRequest.getSort(), pageRequest.getOrder(), pageRequest.getPageNumber(), pageRequest.getPageSize()
                ))
                .map(RenderingUtils::redirectTo);
    }

    @PostMapping(value = "items/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Rendering> importCsvFile(@RequestPart FilePart file, @ModelAttribute PageRequest pageRequest) {
        return adminService.importCsvFile(file)
                .thenReturn(String.format("/items?search=%s&sort=%s&order=%s&pageNumber=%d&pageSize=%d",
                        pageRequest.getSearch(), pageRequest.getSort(), pageRequest.getOrder(), pageRequest.getPageNumber(), pageRequest.getPageSize()
                ))
                .map(RenderingUtils::redirectTo);
    }
}