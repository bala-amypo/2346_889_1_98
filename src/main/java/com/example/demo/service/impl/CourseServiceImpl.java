// package com.example.demo.service.impl;

// import com.example.demo.model.Course;
// import com.example.demo.model.User;
// import com.example.demo.repository.CourseRepository;
// import com.example.demo.repository.UserRepository;
// import org.springframework.stereotype.Service;

// @Service
// public class CourseServiceImpl {
//     private final CourseRepository courseRepository;
//     private final UserRepository userRepository;

//     public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository) {
//         this.courseRepository = courseRepository;
//         this.userRepository = userRepository;
//     }

//     public Course createCourse(Course course, Long instructorId) {
//         User instructor = userRepository.findById(instructorId).orElseThrow(() -> new RuntimeException("No user"));
//         if (courseRepository.existsByTitleAndInstructorId(course.getTitle(), instructorId)) throw new RuntimeException("Title exists");
//         course.setInstructor(instructor);
//         course.prePersist();
//         return courseRepository.save(course);
//     }

//     public Course updateCourse(Long id, Course update) {
//         Course existing = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
//         existing.setTitle(update.getTitle());
//         existing.setDescription(update.getDescription());
//         return courseRepository.save(existing);
//     }

//     public Course getCourse(Long id) {
//         return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
//     }
// }
package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseServiceImpl {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseServiceImpl(CourseRepository cr, UserRepository ur) {
        this.courseRepository = cr; this.userRepository = ur;
    }

    public Course createCourse(Course course, Long instructorId) {
        User instr = userRepository.findById(instructorId).orElseThrow(() -> new RuntimeException("Not found"));
        if (!"INSTRUCTOR".equals(instr.getRole()) && !"ADMIN".equals(instr.getRole())) throw new RuntimeException("Access Denied");
        if (courseRepository.existsByTitleAndInstructorId(course.getTitle(), instructorId)) throw new RuntimeException("Duplicate Title");
        course.setInstructor(instr);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course upd) {
        Course c = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        c.setTitle(upd.getTitle());
        c.setDescription(upd.getDescription());
        return courseRepository.save(c);
    }

    public Course getCourse(Long id) { return courseRepository.findById(id).orElse(null); }
}