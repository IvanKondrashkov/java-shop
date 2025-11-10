package ru.yandex.practicum.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.Action;

@Data
@NoArgsConstructor
public class ActionRequest {
    private Long id;
    private Action action;
}
