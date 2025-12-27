package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RecommendationRequest {

    private BigDecimal confidenceScore;

    private List<String> tags;

    private String targetDifficulty;

    private String contentType;

    private Integer maxItems;
}