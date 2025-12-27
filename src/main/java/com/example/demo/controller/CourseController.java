package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // existing
    @PostMapping
    public Course createCourse(@RequestBody Course course,
                               @RequestParam Long instructorId) {
        return courseService.createCourse(course, instructorId);
    }

    // existing
    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Long id) {
        return courseService.getCourse(id);
    }

    // STEP-5 REQUIRED (added)
    @PutMapping("/{courseId}")
    public Course updateCourse(@PathVariable Long courseId,
                               @RequestBody Course course) {
        return courseService.updateCourse(courseId, course);
    }

    // STEP-5 REQUIRED (added)
    @GetMapping("/instructor/{instructorId}")
    public List<Course> getInstructorCourses(@PathVariable Long instructorId) {
        return courseService.listCoursesByInstructor(instructorId);
    }
}