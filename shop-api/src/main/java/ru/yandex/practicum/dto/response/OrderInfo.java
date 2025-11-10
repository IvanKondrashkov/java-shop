package ru.yandex.practicum.dto.response;

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
    private String status;
    private Set<CartItemInfo> items;
}