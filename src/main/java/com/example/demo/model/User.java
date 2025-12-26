
package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data                // Generates getters/setters
@Builder             // Generates the builder() method
@NoArgsConstructor   // Generates empty constructor
@AllArgsConstructor  // Generates constructor for all fields
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String role;
    private String preferredLearningStyle;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.role == null) this.role = "LEARNER";
    }
}