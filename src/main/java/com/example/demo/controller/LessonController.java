package com.example.demo.controller;

import com.example.demo.model.MicroLesson;
import com.example.demo.service.LessonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    // existing
    @PostMapping("/{courseId}")
    public MicroLesson addLesson(@PathVariable Long courseId,
                                 @RequestBody MicroLesson lesson) {
        return lessonService.addLesson(courseId, lesson);
    }

    // existing
    @GetMapping("/search")
    public List<MicroLesson> searchLessons(@RequestParam(required = false) String tags,
                                           @RequestParam(required = false) String difficulty,
                                           @RequestParam(required = false) String contentType) {
        return lessonService.findLessonsByFilters(tags, difficulty, contentType);
    }

    // STEP-5 REQUIRED (added)
    @PutMapping("/{lessonId}")
    public MicroLesson updateLesson(@PathVariable Long lessonId,
                                    @RequestBody MicroLesson lesson) {
        return lessonService.updateLesson(lessonId, lesson);
    }

    // STEP-5 REQUIRED (added)
    @GetMapping("/{lessonId}")
    public MicroLesson getLesson(@PathVariable Long lessonId) {
        return lessonService.getLesson(lessonId);
    }
}