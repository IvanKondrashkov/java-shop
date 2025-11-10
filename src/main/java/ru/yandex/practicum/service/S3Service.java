package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.response.ImageInfo;
public interface S3Service {
    ImageInfo uploadImage(String fileName, byte[] image);
    byte[] downloadImage(String fileName);
    void deleteImage(String fileName);
}