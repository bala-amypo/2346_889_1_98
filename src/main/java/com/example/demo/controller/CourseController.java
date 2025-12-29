// package com.example.demo.controller;

// import com.example.demo.model.Course;
// import com.example.demo.service.impl.CourseServiceImpl;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/courses")
// @RequiredArgsConstructor
// @Tag(name = "Course Management")
// public class CourseController {

//     private final CourseServiceImpl courseService;

//     @PostMapping
//     public ResponseEntity<Course> createCourse(@RequestBody Course course, @RequestParam Long instructorId) {
//         return ResponseEntity.ok(courseService.createCourse(course, instructorId));
//     }

//     @PutMapping("/{courseId}")
//     public ResponseEntity<Course> updateCourse(@PathVariable Long courseId, @RequestBody Course course) {
//         return ResponseEntity.ok(courseService.updateCourse(courseId, course));
//     }

//     @GetMapping("/{courseId}")
//     public ResponseEntity<Course> getCourse(@PathVariable Long courseId) {
//         return ResponseEntity.ok(courseService.getCourse(courseId));
//     }
// }
package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.service.impl.CourseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "2. Course Management", description = "Instructor/Admin only endpoints for Courses")
public class CourseController {

    private final CourseServiceImpl courseService;

    @PostMapping
    @Operation(summary = "Create a new course (Requires Instructor/Admin role)")
    public ResponseEntity<Course> createCourse(@RequestBody Course course, @RequestParam Long instructorId) {
        return ResponseEntity.ok(courseService.createCourse(course, instructorId));
    }

    @PutMapping("/{courseId}")
    @Operation(summary = "Update an existing course")
    public ResponseEntity<Course> updateCourse(@PathVariable Long courseId, @RequestBody Course course) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, course));
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get course details by ID")
    public ResponseEntity<Course> getCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourse(courseId));
    }
}