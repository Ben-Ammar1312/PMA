package com.example.backend.service;

import com.example.backend.exception.UserRegistrationException;
import com.example.backend.model.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<String> register(RegisterRequest request) {
        if (repository.findByEmail(request.email()).isPresent()) {
            throw new UserRegistrationException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(List.of("Patient"));
        user.setSubmitted(false);

        User saved = repository.save(user);
        return List.of(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getEmail());
    }
}

