package com.example.backend.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for user registration.
 */
public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String lastName,
        @NotBlank String firstName,
        @NotBlank String password
) {}

