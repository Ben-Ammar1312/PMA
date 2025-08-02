package com.example.backend.service;

import com.example.backend.exception.UserRegistrationException;
import com.example.backend.model.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock private UserRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserRegistrationService registrationService;

    @Test
    void register_savesUserAndReturnsInfo() {
        RegisterRequest req = new RegisterRequest("test@example.com", "Doe", "John", "pass");
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("enc");
        User saved = new User();
        saved.setId("123");
        saved.setEmail("test@example.com");
        saved.setFirstName("John");
        saved.setLastName("Doe");
        saved.setPassword("enc");
        saved.setRoles(List.of("Patient"));
        saved.setSubmitted(false);
        when(repository.save(any(User.class))).thenReturn(saved);

        List<String> info = registrationService.register(req);

        assertEquals(List.of("123", "John", "Doe", "test@example.com"), info);
        verify(repository).save(any(User.class));
    }

    @Test
    void register_existingEmail_throwsException() {
        RegisterRequest req = new RegisterRequest("test@example.com", "Doe", "John", "pass");
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(UserRegistrationException.class, () -> registrationService.register(req));
    }
}

