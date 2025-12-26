package com.example.demo.service;

import com.example.demo.model.MicroLesson;

import java.util.List;

public interface LessonService {
    MicroLesson addLesson(Long courseId, MicroLesson lesson);
    MicroLesson updateLesson(Long lessonId, MicroLesson lesson);
    MicroLesson getLesson(Long id);
    List<MicroLesson> findLessonsByFilters(String tags, String difficulty, String contentType);
}