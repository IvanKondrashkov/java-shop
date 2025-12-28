package ru.yandex.practicum.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "payments")
public class Payment {
    @Id
    private Long id;
    @Column
    private String currency;
    @Column
    private BigDecimal amount;
    @Column
    private String status;
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("user_id")
    private Long userId;
    @Column("order_id")
    private Long orderId;
}