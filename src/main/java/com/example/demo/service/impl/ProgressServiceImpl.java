
package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProgressServiceImpl {
    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final MicroLessonRepository lessonRepository;

    public ProgressServiceImpl(ProgressRepository pr, UserRepository ur, MicroLessonRepository lr) {
        this.progressRepository = pr; this.userRepository = ur; this.lessonRepository = lr;
    }

    public Progress recordProgress(Long userId, Long lessonId, Progress incoming) {
        User u = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        MicroLesson m = lessonRepository.findById(lessonId).orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        Progress p = progressRepository.findByUserIdAndMicroLessonId(userId, lessonId)
                .orElse(Progress.builder().user(u).microLesson(m).build());

        p.setStatus(incoming.getStatus());
        p.setProgressPercent(incoming.getProgressPercent());
        p.setScore(incoming.getScore());
        
        // COMPLETED rule
        if ("COMPLETED".equals(p.getStatus())) p.setProgressPercent(100);
        
        p.prePersist(); 
        return progressRepository.save(p);
    }

    public List<Progress> getUserProgress(Long userId) { return progressRepository.findByUserIdOrderByLastAccessedAtDesc(userId); }
}