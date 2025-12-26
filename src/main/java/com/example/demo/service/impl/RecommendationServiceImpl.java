
package com.example.demo.service.impl;

import com.example.demo.dto.RecommendationRequest;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecommendationServiceImpl {
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final MicroLessonRepository lessonRepository;

    public RecommendationServiceImpl(RecommendationRepository rr, UserRepository ur, MicroLessonRepository lr) {
        this.recommendationRepository = rr; this.userRepository = ur; this.lessonRepository = lr;
    }

    public Recommendation getLatestRecommendation(Long userId) {
        List<Recommendation> list = recommendationRepository.findByUserIdOrderByGeneratedAtDesc(userId);
        if (list.isEmpty()) throw new RuntimeException("No rec found");
        return list.get(0);
    }
}