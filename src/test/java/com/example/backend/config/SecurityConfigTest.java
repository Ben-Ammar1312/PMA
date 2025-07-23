package com.example.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    @Test
    void jwtAuthenticationConverter_extractsRealmRoles() {
        // Build the converter directly (isolated test)
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realm = jwt.getClaim("realm_access");
            if (realm == null) return java.util.List.of();
            @SuppressWarnings("unchecked")
            var roles = (java.util.List<String>) realm.getOrDefault("roles", java.util.List.of());
            return roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(java.util.stream.Collectors.toSet());
        });

        // Mock JWT with realm_access
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "none"),
                Map.of("realm_access", Map.of("roles", java.util.List.of("Doctor")))
        );

        var auth = converter.convert(jwt);
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_Doctor")));
    }
}
