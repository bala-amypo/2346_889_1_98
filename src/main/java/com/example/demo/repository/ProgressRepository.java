package com.example.demo.repository;

import com.example.demo.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByUserIdAndMicroLessonId(Long userId, Long microLessonId);

    List<Progress> findByUserIdOrderByLastAccessedAtDesc(Long userId);
}