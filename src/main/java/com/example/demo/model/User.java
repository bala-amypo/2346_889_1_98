package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String role;

    private String preferredLearningStyle;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "instructor")
    private List<Course> courses;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
