package ru.yandex.practicum.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequest {
    private Long userId = 1L;
    private String firstName = "Djon";
    private String lastName = "Doe";
}