package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    private Integer progressPercent;

    private BigDecimal score;

    private LocalDateTime lastAccessedAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private MicroLesson microLesson;

    @PrePersist
    void onCreate() {
        this.lastAccessedAt = LocalDateTime.now();
    }
}
