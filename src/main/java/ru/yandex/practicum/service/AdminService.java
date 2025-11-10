package ru.yandex.practicum.service;

import org.springframework.web.multipart.MultipartFile;

public interface AdminService {
    void importCsvFile(MultipartFile file);
}