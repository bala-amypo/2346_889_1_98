package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "micro_lessons")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MicroLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer durationMinutes;

    private String contentType;

    private String difficulty;

    private String tags;

    private LocalDate publishDate;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
