package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private MicroLesson microLesson;

    private Integer progressPercent;

    private String status;

    private BigDecimal score;

    private LocalDateTime lastAccessedAt;

    @PrePersist
    public void prePersist() {
        this.lastAccessedAt = LocalDateTime.now();
    }
}