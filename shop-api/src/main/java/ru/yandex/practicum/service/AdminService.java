package ru.yandex.practicum.service;

import reactor.core.publisher.Mono;
import org.springframework.http.codec.multipart.FilePart;

public interface AdminService {
    Mono<Void> importCsvFile(FilePart file);
}