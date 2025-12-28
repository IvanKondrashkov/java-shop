package ru.yandex.practicum.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.SortType;
import ru.yandex.practicum.dto.OrderType;

@Data
@NoArgsConstructor
public class PageRequest {
    private String search = "";
    private SortType sort = SortType.NO;
    private OrderType order = OrderType.DESC;
    private Integer pageNumber = 1;
    private Integer pageSize = 5;
}