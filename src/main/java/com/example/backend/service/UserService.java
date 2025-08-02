package com.example.backend.service;

import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void markSubmitted(String userId) {
        repository.findById(userId).ifPresent(user -> {
            user.setSubmitted(true);
            repository.save(user);
        });
    }
}
