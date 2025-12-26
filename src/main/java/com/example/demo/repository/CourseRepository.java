
package com.example.demo.repository;

import com.example.demo.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    boolean existsByTitleAndInstructorId(String title, Long instructorId);
    List<Course> findByInstructorId(Long instructorId);
}