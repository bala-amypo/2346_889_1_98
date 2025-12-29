// package com.example.demo.dto;
// import lombok.*;

// @Data @Builder @AllArgsConstructor @NoArgsConstructor
// public class RecommendationRequest {
//     private String tags;
//     private String difficulty;
//     private String contentType;
// }
package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    private String tags;
    private String difficulty;
    private String contentType;
    private Integer limit;
}