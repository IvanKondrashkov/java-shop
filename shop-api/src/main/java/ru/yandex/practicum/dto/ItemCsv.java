package ru.yandex.practicum.dto;

import lombok.*;
import java.math.BigDecimal;
import com.opencsv.bean.CsvBindByName;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCsv {
    @CsvBindByName(column = "title", required = true)
    private String title;
    @CsvBindByName(column = "description", required = true)
    private String description;
    @CsvBindByName(column = "price", required = true)
    private BigDecimal price;
    @CsvBindByName(column = "image", required = true)
    private String imageBase64;
}