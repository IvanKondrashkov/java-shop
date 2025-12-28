package ru.yandex.practicum.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "cart_items")
public class CartItem {
    @Id
    private Long id;
    @Column
    private Integer quantity;
    @Column("user_id")
    private Long userId;
    @Column("item_id")
    private Long itemId;
    @Column("order_id")
    private Long orderId;
}