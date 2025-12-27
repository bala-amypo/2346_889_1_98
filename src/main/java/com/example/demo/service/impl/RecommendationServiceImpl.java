package com.example.demo.service.impl;

import com.example.demo.dto.RecommendationRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final MicroLessonRepository microLessonRepository;

    @Autowired
    private ProgressRepository progressRepository;

    // ✅ THIS constructor matches the TEST CASE exactly
    public RecommendationServiceImpl(
            RecommendationRepository recommendationRepository,
            UserRepository userRepository,
            MicroLessonRepository microLessonRepository) {

        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
        this.microLessonRepository = microLessonRepository;
    }

    @Override
    public Recommendation generateRecommendation(Long userId, RecommendationRequest params) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Progress> progressList =
                progressRepository.findByUserIdOrderByLastAccessedAtDesc(userId);

        String tags = (params.getTags() == null || params.getTags().isEmpty())
                ? null
                : String.join(",", params.getTags());

        List<MicroLesson> candidates = microLessonRepository.findByFilters(
                tags,
                params.getTargetDifficulty(),
                params.getContentType()
        );

        List<Long> completedIds = progressList.stream()
                .filter(p -> "COMPLETED".equals(p.getStatus()))
                .map(p -> p.getMicroLesson().getId())
                .collect(Collectors.toList());

        List<MicroLesson> recommended = candidates.stream()
                .filter(l -> !completedIds.contains(l.getId()))
                .limit(params.getMaxItems() != null ? params.getMaxItems() : 5)
                .collect(Collectors.toList());

        String ids = recommended.stream()
                .map(l -> l.getId().toString())
                .collect(Collectors.joining(","));

        BigDecimal confidence = calculateConfidenceScore(
                recommended.size(),
                progressList.size()
        );

        Recommendation rec = Recommendation.builder()
                .user(user)
                .recommendedLessonIds(ids)
                .confidenceScore(confidence)
                .build();

        return recommendationRepository.save(rec);
    }

    @Override
    public Recommendation getLatestRecommendation(Long userId) {
        List<Recommendation> list =
                recommendationRepository.findByUserIdOrderByGeneratedAtDesc(userId);

        if (list.isEmpty()) {
            throw new ResourceNotFoundException("No recommendations found");
        }
        return list.get(0);
    }

    @Override
    public List<Recommendation> getRecommendations(Long userId, LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);
        return recommendationRepository.findByUserIdAndGeneratedAtBetween(userId, start, end);
    }

    private BigDecimal calculateConfidenceScore(int recCount, int progCount) {
        double score = Math.min(1.0, recCount / 5.0);
        score += Math.min(0.5, progCount / 20.0);
        return BigDecimal.valueOf(Math.max(0.1, Math.min(1.0, score)));
    }
}