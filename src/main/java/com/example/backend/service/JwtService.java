package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.model.requests.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder encoder;

    @Value("${jwt.expiration:3600}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-expiration:86400}")
    private long refreshTokenValidity;

    public LoginResponse generate(User user) {
        Instant now = Instant.now();

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .subject(user.getId())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenValidity))
                .claim("preferred_username", user.getEmail())
                .claim("email", user.getEmail())
                .claim("given_name", user.getFirstName())
                .claim("family_name", user.getLastName())
                .claim("realm_access", Map.of("roles", user.getRoles()))
                .claim("submitted", user.isSubmitted())
                .build();

        String accessToken = encoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .subject(user.getId())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenValidity))
                .claim("type", "refresh")
                .build();

        String refreshToken = encoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

        return new LoginResponse(accessToken, accessTokenValidity, refreshToken, refreshTokenValidity, "Bearer");
    }
}

