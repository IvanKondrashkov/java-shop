package ru.yandex.practicum.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageInfo {
    private String fileName;
    private String imageUrl;
}