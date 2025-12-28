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
@Table(name = "items")
public class Item {
    @Id
    private Long id;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private BigDecimal price;
    @Column("image_id")
    private Long imageId;
}