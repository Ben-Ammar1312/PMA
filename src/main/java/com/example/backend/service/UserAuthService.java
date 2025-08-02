package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.model.requests.LoginResponse;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(String username, String password) {
        User user = repository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return jwtService.generate(user);
    }

    public void markSubmitted(String userId) {
        repository.findById(userId).ifPresent(user -> {
            user.setSubmitted(true);
            repository.save(user);
        });
    }
}

