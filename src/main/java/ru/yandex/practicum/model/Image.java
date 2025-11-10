package ru.yandex.practicum.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}