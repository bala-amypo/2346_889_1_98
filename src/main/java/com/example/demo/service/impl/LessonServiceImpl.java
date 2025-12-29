// package com.example.demo.service.impl;

// import com.example.demo.model.Course;
// import com.example.demo.model.MicroLesson;
// import com.example.demo.repository.CourseRepository;
// import com.example.demo.repository.MicroLessonRepository;
// import org.springframework.stereotype.Service;
// import java.util.List;

// @Service
// public class LessonServiceImpl {
//     private final MicroLessonRepository lessonRepo;
//     private final CourseRepository courseRepo;

//     public LessonServiceImpl(MicroLessonRepository lr, CourseRepository cr) {
//         this.lessonRepo = lr;
//         this.courseRepo = cr;
//     }

//     public MicroLesson addLesson(Long courseId, MicroLesson lesson) {
//         Course course = courseRepo.findById(courseId).orElseThrow(() -> new RuntimeException("Not found"));
//         lesson.setCourse(course);
//         return lessonRepo.save(lesson);
//     }

//     public MicroLesson updateLesson(Long id, MicroLesson update) {
//         MicroLesson existing = lessonRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
//         existing.setTitle(update.getTitle());
//         existing.setContentType(update.getContentType());
//         existing.setDifficulty(update.getDifficulty());
//         return lessonRepo.save(existing);
//     }

//     public List<MicroLesson> findLessonsByFilters(String t, String d, String c) {
//         return lessonRepo.findByFilters(t, d, c);
//     }

//     public MicroLesson getLesson(Long id) {
//         return lessonRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
//     }
// }
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