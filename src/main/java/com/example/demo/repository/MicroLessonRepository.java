package com.example.demo.repository;

import com.example.demo.model.MicroLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MicroLessonRepository extends JpaRepository<MicroLesson, Long> {

    @Query("""
           SELECT m FROM MicroLesson m
           WHERE (:tags IS NULL OR m.tags LIKE %:tags%)
             AND (:difficulty IS NULL OR m.difficulty = :difficulty)
             AND (:contentType IS NULL OR m.contentType = :contentType)
           """)
    List<MicroLesson> findByFilters(String tags, String difficulty, String contentType);
}