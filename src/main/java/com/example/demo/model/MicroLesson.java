package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MicroLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Course course;

    private String title;

    private Integer durationMinutes;

    private String contentType; // VIDEO / TEXT

    private String difficulty; // BEGINNER / INTERMEDIATE / ADVANCED

    private String tags;

    private LocalDate publishDate;
}