package ru.yandex.practicum.repository;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.model.Item;
import org.springframework.stereotype.Repository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {
    @Query("""
    SELECT i.* FROM items i 
    ORDER BY 
        CASE WHEN :sort = 'title' THEN i.title END,
        CASE WHEN :sort = 'price' THEN i.price END
    LIMIT :limit OFFSET :offset
    """)
    Flux<Item> findAll(Integer limit, Integer offset, String sort);
    @Query("""
    SELECT i.* FROM items i 
    WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :title, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :description, '%'))
    ORDER BY 
        CASE WHEN :sort = 'title' THEN i.title END,
        CASE WHEN :sort = 'price' THEN i.price END
    LIMIT :limit OFFSET :offset
    """)
    Flux<Item> findAllBySearch(String title, String description, Integer limit, Integer offset, String sort);
    @Query("""
    SELECT COUNT(*) FROM items i 
    WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :title, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :description, '%'))
    """)
    Mono<Long> countBySearch(String title, String description);
}