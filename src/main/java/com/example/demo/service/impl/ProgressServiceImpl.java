package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.MicroLesson;
import com.example.demo.model.Progress;
import com.example.demo.model.User;
import com.example.demo.repository.MicroLessonRepository;
import com.example.demo.repository.ProgressRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProgressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final MicroLessonRepository microLessonRepository;

    public ProgressServiceImpl(ProgressRepository progressRepository,
                               UserRepository userRepository,
                               MicroLessonRepository microLessonRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.microLessonRepository = microLessonRepository;
    }

    @Override
    public Progress recordProgress(Long userId, Long lessonId, Progress progress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MicroLesson lesson = microLessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        Optional<Progress> existing =
                progressRepository.findByUserIdAndMicroLessonId(userId, lessonId);

        Progress toSave = existing.orElseGet(() ->
                Progress.builder()
                        .user(user)
                        .microLesson(lesson)
                        .build()
        );

        toSave.setStatus(progress.getStatus());
        toSave.setProgressPercent(progress.getProgressPercent());
        toSave.setScore(progress.getScore());

        return progressRepository.save(toSave);
    }

    @Override
    public Progress getProgress(Long userId, Long lessonId) {
        return progressRepository.findByUserIdAndMicroLessonId(userId, lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found"));
    }

    @Override
    public List<Progress> getUserProgress(Long userId) {
        return progressRepository.findByUserIdOrderByLastAccessedAtDesc(userId);
    }
}