package ru.yandex.practicum.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Page {
    private Integer pageNumber;
    private Integer pageSize;
    private Integer offset;
    private Boolean hasNext;
    private Boolean hasPrevious;
}