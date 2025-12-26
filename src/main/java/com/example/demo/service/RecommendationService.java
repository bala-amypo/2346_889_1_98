package com.example.demo.service;

import com.example.demo.model.Recommendation;

public interface RecommendationService {
    Recommendation getLatestRecommendation(Long userId);
}