package ru.yandex.practicum.model;

import lombok.*;
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
@Table(name = "images")
public class Image {
    @Id
    private Long id;
    @Column("file_name")
    private String fileName;
    @Column("image_url")
    private String imageUrl;
    @Column("created_at")
    private LocalDateTime createdAt;
}