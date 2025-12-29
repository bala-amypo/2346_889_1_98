// package com.example.demo.service;

// import com.example.demo.model.MicroLesson;
// import java.util.List;

// public interface LessonService {

//     MicroLesson addLesson(Long courseId, MicroLesson lesson);

//     MicroLesson updateLesson(Long lessonId, MicroLesson lesson);

//     // ✅ unified search method
//     List<MicroLesson> search(String tags, String difficulty, String contentType);

//     MicroLesson getLesson(Long lessonId);
// }
package com.example.demo.service;

import com.example.demo.model.MicroLesson;

import java.util.List;

public interface LessonService {
    MicroLesson addLesson(Long courseId, MicroLesson lesson);
    MicroLesson updateLesson(Long lessonId, MicroLesson lesson);
    List<MicroLesson> findLessonsByFilters(String tags, String difficulty, String contentType);
    MicroLesson getLesson(Long lessonId);
}