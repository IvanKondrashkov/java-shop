package ru.yandex.practicum.model;

import lombok.*;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "balances")
public class Balance {
    @Id
    private Long id;
    @Column
    private String currency;
    @Column
    private BigDecimal balance;
    @Column("user_id")
    private Long userId;
}