package ru.yandex.practicum.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "orders")
public class Order {
    @Id
    private Long id;
    @Column("total_sum")
    private BigDecimal totalSum;
    @Column
    private String status;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("user_id")
    private Long userId;
}