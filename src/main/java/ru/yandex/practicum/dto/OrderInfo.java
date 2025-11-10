package ru.yandex.practicum.dto;

import lombok.*;
import java.util.Set;
import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {
    private Long id;
    private BigDecimal totalSum;
    private Set<CartItemInfo> items;
}