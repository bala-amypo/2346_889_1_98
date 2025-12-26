
package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime generatedAt;

    private String recommendedLessonIds;

    private String basisSnapshot;

    private BigDecimal confidenceScore;

    @PrePersist
    public void prePersist() {
        this.generatedAt = LocalDateTime.now();
    }
}