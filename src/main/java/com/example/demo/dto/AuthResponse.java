// package com.example.demo.dto;

// import lombok.*;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class AuthResponse {
//     private String accessToken;
//     private Long userId;
//     private String email;
//     private String role;
// }

// 2nd one
// package com.example.demo.dto;

// import lombok.*;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class AuthResponse {
//     private String accessToken;
//     private Long userId;
//     private String email;
//     private String role;
// }

package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private Long userId;
    private String email;
    private String role;
}