package com.example.demo.controller;

import com.example.demo.model.Recommendation;
import com.example.demo.service.impl.RecommendationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Tag(name = "5. Recommendations", description = "Generate and fetch AI content suggestions")
public class RecommendationController {

    private final RecommendationServiceImpl recommendationService;

    @GetMapping("/latest")
    @Operation(summary = "Fetch the most recent recommendation for the user")
    public ResponseEntity<Recommendation> getLatest(@RequestParam Long userId) {
        return ResponseEntity.ok(recommendationService.getLatestRecommendation(userId));
    }
}