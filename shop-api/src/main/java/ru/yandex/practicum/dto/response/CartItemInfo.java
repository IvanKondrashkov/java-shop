package ru.yandex.practicum.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemInfo {
    private Long id;
    private String title;
    private Integer quantity;
    private BigDecimal price;
}