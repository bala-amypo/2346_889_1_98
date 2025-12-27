package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String category;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "course")
    private List<MicroLesson> lessons;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
