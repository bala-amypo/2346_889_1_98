`package com.example.demo.controller;

import com.example.demo.dto.RecommendationRequest;
import com.example.demo.model.Recommendation;
import com.example.demo.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    // existing
    @PostMapping("/{userId}")
    public Recommendation generate(@PathVariable Long userId,
                                   @RequestBody RecommendationRequest request) {
        return recommendationService.generateRecommendation(userId, request);
    }

    // existing
    @GetMapping("/latest/{userId}")
    public Recommendation latest(@PathVariable Long userId) {
        return recommendationService.getLatestRecommendation(userId);
    }

    // STEP-5 REQUIRED (added)
    @GetMapping("/user/{userId}")
    public List<Recommendation> getUserRecommendations(@PathVariable Long userId) {
        return recommendationService.getRecommendations(userId, null, null);
    }
}