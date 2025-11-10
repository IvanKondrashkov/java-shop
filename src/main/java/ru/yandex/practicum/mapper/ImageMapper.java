package ru.yandex.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import ru.yandex.practicum.model.Image;
import ru.yandex.practicum.dto.ImageInfo;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ImageMapper {
    public static ImageInfo imageToImageInfo(Image image) {
        return ImageInfo.builder()
                .fileName(image.getFileName())
                .imageUrl(image.getImageUrl())
                .build();
    }

    public static Image imageInfoToImage(ImageInfo imageInfo) {
        return Image.builder()
                .fileName(imageInfo.getFileName())
                .imageUrl(imageInfo.getImageUrl())
                .createdAt(LocalDateTime.now())
                .build();
    }
}