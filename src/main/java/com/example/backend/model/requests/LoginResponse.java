package com.example.backend.model.requests;

/**
 * Response body returned after a successful login.
 */
public record LoginResponse(
        String accessToken,
        long expiresIn,
        String refreshToken,
        long refreshExpiresIn,
        String tokenType
) {}

