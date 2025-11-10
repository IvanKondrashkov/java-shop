package ru.yandex.practicum.repository;

import java.util.List;
import java.util.Optional;
import ru.yandex.practicum.model.CartItem;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByItem_Id(Long itemId);
    List<CartItem> findAllByOrderIsNull();
    @Query(value = "SELECT quantity FROM cart_items WHERE item_id = :itemId AND order_id IS NULL", nativeQuery = true)
    Integer countByItem_Id(@Param("itemId") Long itemId);
    void deleteByItem_Id(Long itemId);
}