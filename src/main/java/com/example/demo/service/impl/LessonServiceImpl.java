package com.example.demo.service.impl;

import com.example.demo.model.Course;
import com.example.demo.model.MicroLesson;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.MicroLessonRepository;
import com.example.demo.service.LessonService;

import java.util.List;

public class LessonServiceImpl implements LessonService {

    private final MicroLessonRepository microLessonRepository;
    private final CourseRepository courseRepository;

    public LessonServiceImpl(MicroLessonRepository microLessonRepository,
                             CourseRepository courseRepository) {
        this.microLessonRepository = microLessonRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public MicroLesson addLesson(Long courseId, MicroLesson lesson) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(RuntimeException::new);

        lesson.setCourse(course);
        return microLessonRepository.save(lesson);
    }

    @Override
    public MicroLesson updateLesson(Long lessonId, MicroLesson lesson) {
        MicroLesson existing = microLessonRepository.findById(lessonId)
                .orElseThrow(RuntimeException::new);

        existing.setTitle(lesson.getTitle());
        existing.setDifficulty(lesson.getDifficulty());
        existing.setContentType(lesson.getContentType());
        return microLessonRepository.save(existing);
    }

    @Override
    public MicroLesson getLesson(Long id) {
        return microLessonRepository.findById(id)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public List<MicroLesson> findLessonsByFilters(String tags, String difficulty, String contentType) {
        return microLessonRepository.findByFilters(tags, difficulty, contentType);
    }
}