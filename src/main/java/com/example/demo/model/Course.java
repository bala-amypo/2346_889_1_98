
package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "courses")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @ManyToOne @JoinColumn(name = "instructor_id")
    private User instructor;
    private String category;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }
}