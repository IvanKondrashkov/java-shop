package ru.yandex.practicum.repository;

import ru.yandex.practicum.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);
}