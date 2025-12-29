// package com.example.demo.controller;

// import com.example.demo.dto.AuthResponse;
// import com.example.demo.model.User;
// import com.example.demo.service.impl.UserServiceImpl;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.Map;

// @RestController
// @RequestMapping("/auth")
// @RequiredArgsConstructor
// @Tag(name = "Authentication")
// public class AuthController {

//     private final UserServiceImpl userService;

//     @PostMapping("/register")
//     public ResponseEntity<User> register(@RequestBody User user) {
//         // Calls the register(User) method in service
//         return ResponseEntity.ok(userService.register(user));
//     }

//     @PostMapping("/login")
//     public ResponseEntity<AuthResponse> login(@RequestBody Map<String, String> loginRequest) {
//         String email = loginRequest.get("email");
//         String password = loginRequest.get("password");
//         // Calls the login(String, String) method in service
//         return ResponseEntity.ok(userService.login(email, password));
//     }
// }


// // 2nd one
// package com.example.demo.controller;

// import com.example.demo.dto.AuthResponse;
// import com.example.demo.model.User;
// import com.example.demo.service.impl.UserServiceImpl;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.Map;

// @RestController
// @RequestMapping("/auth")
// @RequiredArgsConstructor
// @Tag(name = "1. Authentication", description = "Endpoints for User Registration and Login")
// public class AuthController {

//     private final UserServiceImpl userService;

//     @PostMapping("/register")
//     @Operation(summary = "Register a new user (Learner/Instructor/Admin)")
//     public ResponseEntity<User> register(@RequestBody User user) {
//         return ResponseEntity.ok(userService.register(user));
//     }

//     @PostMapping("/login")
//     @Operation(summary = "Login to get JWT Access Token")
//     public ResponseEntity<AuthResponse> login(@RequestBody Map<String, String> loginRequest) {
//         return ResponseEntity.ok(userService.login(loginRequest.get("email"), loginRequest.get("password")));
//     }
// }

package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;  // Import the new DTO
import com.example.demo.dto.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    @Operation(summary = "Login to get JWT Token")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest loginRequest) {
        // Now using loginRequest.getEmail() and loginRequest.getPassword()
        return ResponseEntity.ok(userService.login(loginRequest.getEmail(), loginRequest.getPassword()));
    }
}