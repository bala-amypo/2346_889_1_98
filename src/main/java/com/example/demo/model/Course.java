package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String category;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    @JsonIgnoreProperties({"courses"}) // 🔥 FIX
    private User instructor;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}