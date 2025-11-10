package ru.yandex.practicum.utils;

import java.util.List;
import lombok.AccessLevel;
import java.math.BigDecimal;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.response.ItemInfo;
import org.springframework.web.reactive.result.view.Rendering;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RenderingUtils {
    public static Mono<Rendering> renderCart(List<ItemInfo> items, BigDecimal total, Boolean isSufficient) {
        return Mono.just(Rendering.view("cart")
                .modelAttribute("items", items)
                .modelAttribute("total", total)
                .modelAttribute("isSufficient", isSufficient)
                .build());
    }

    public static Rendering renderItem(ItemInfo item) {
        return Rendering.view("item")
                .modelAttribute("item", item)
                .build();
    }

    public static Rendering redirectTo(String url) {
        return Rendering.redirectTo(url).build();
    }
}