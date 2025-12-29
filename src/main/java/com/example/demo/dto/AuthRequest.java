// package com.example.demo.dto;

// import lombok.*;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class AuthRequest {
//     private String email;
//     private String password;
// }

package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
}