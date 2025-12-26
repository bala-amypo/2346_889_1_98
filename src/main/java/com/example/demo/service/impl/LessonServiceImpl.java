
package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LessonServiceImpl {
    private final MicroLessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    public LessonServiceImpl(MicroLessonRepository lr, CourseRepository cr) {
        this.lessonRepository = lr; this.courseRepository = cr;
    }

    public MicroLesson addLesson(Long courseId, MicroLesson lesson) {
        Course c = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Not found"));
        if (lesson.getDurationMinutes() != null && lesson.getDurationMinutes() > 15) throw new RuntimeException("Too long");
        lesson.setCourse(c);
        return lessonRepository.save(lesson);
    }

    public MicroLesson updateLesson(Long id, MicroLesson upd) {
        MicroLesson m = lessonRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        m.setTitle(upd.getTitle());
        m.setContentType(upd.getContentType());
        m.setDifficulty(upd.getDifficulty());
        return lessonRepository.save(m);
    }

    public List<MicroLesson> findLessonsByFilters(String t, String d, String c) { return lessonRepository.findByFilters(t, d, c); }
    public MicroLesson getLesson(Long id) { return lessonRepository.findById(id).orElse(null); }
}