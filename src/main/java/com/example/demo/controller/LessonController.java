// package com.example.demo.controller;

// import com.example.demo.model.MicroLesson;
// import com.example.demo.service.impl.LessonServiceImpl;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/lessons")
// @RequiredArgsConstructor
// @Tag(name = "Lesson Management")
// public class LessonController {

//     private final LessonServiceImpl lessonService;

//     @PostMapping("/course/{courseId}")
//     public ResponseEntity<MicroLesson> addLesson(@PathVariable Long courseId, @RequestBody MicroLesson lesson) {
//         return ResponseEntity.ok(lessonService.addLesson(courseId, lesson));
//     }

//     @PutMapping("/{lessonId}")
//     public ResponseEntity<MicroLesson> updateLesson(@PathVariable Long lessonId, @RequestBody MicroLesson lesson) {
//         return ResponseEntity.ok(lessonService.updateLesson(lessonId, lesson));
//     }

//     @GetMapping("/search")
//     public ResponseEntity<List<MicroLesson>> searchLessons(
//             @RequestParam String tags,
//             @RequestParam String difficulty,
//             @RequestParam String contentType) {
//         return ResponseEntity.ok(lessonService.findLessonsByFilters(tags, difficulty, contentType));
//     }

//     @GetMapping("/{lessonId}")
//     public ResponseEntity<MicroLesson> getLesson(@PathVariable Long lessonId) {
//         return ResponseEntity.ok(lessonService.getLesson(lessonId));
//     }
// }
package com.example.demo.controller;

import com.example.demo.model.MicroLesson;
import com.example.demo.service.impl.LessonServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
@Tag(name = "3. Lesson Management", description = "Manage Micro-Lessons and search content")
public class LessonController {

    private final LessonServiceImpl lessonService;

    @PostMapping("/course/{courseId}")
    @Operation(summary = "Add a micro-lesson to a specific course")
    public ResponseEntity<MicroLesson> addLesson(@PathVariable Long courseId, @RequestBody MicroLesson lesson) {
        return ResponseEntity.ok(lessonService.addLesson(courseId, lesson));
    }

    @PutMapping("/{lessonId}")
    @Operation(summary = "Update lesson details")
    public ResponseEntity<MicroLesson> updateLesson(@PathVariable Long lessonId, @RequestBody MicroLesson lesson) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, lesson));
    }

    @GetMapping("/search")
    @Operation(summary = "Search lessons by Tags, Difficulty, and Content Type")
    public ResponseEntity<List<MicroLesson>> search(
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String contentType) {
        return ResponseEntity.ok(lessonService.findLessonsByFilters(tags, difficulty, contentType));
    }

    @GetMapping("/{lessonId}")
    @Operation(summary = "Get lesson by ID")
    public ResponseEntity<MicroLesson> getLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(lessonService.getLesson(lessonId));
    }
}