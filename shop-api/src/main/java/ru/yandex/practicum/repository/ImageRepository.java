package ru.yandex.practicum.repository;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.model.Image;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

@Repository
public interface ImageRepository extends R2dbcRepository<Image, Long> {
    @Query("SELECT img.* FROM images img JOIN items i ON i.image_id = img.id WHERE i.id = :itemId")
    Mono<Image> findByItemId(Long itemId);
}