
package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.model.User;

public interface UserService {
    User register(User user);
    AuthResponse login(String email, String password);
    User findById(Long id);
    User findByEmail(String email);
}