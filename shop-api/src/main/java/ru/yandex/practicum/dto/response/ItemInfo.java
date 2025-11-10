package ru.yandex.practicum.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfo {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private ImageInfo image;
    private Integer count;
}