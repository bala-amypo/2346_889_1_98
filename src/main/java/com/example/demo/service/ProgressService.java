package com.example.demo.service;

import com.example.demo.model.Progress;

import java.util.List;

public interface ProgressService {
    Progress recordProgress(Long userId, Long lessonId, Progress progress);
    List<Progress> getUserProgress(Long userId);
}