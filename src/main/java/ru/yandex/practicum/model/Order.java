package ru.yandex.practicum.model;

import lombok.*;
import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "total_sum", nullable = false)
    private BigDecimal totalSum;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<CartItem> items = new HashSet<>();
}