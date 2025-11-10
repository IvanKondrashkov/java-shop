package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.Image;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}