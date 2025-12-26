
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