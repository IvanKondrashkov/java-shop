package ru.yandex.practicum.service;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.dto.response.ImageInfo;

public interface S3Service {
    Mono<ImageInfo> uploadImage(String fileName, byte[] image);
    Mono<byte[]> downloadImage(String fileName);
    Mono<Void> deleteImage(String fileName);
}