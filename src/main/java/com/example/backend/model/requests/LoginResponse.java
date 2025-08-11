package com.example.backend.model.requests;

public record LoginResponse(
        String accessToken,
        long expiresIn,
        String refreshToken,
        long refreshExpiresIn,
        String tokenType
) {}
