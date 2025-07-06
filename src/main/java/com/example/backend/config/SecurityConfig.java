package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity          // enables @PreAuthorize, @PostAuthorize, …
public class SecurityConfig {

    /** Extract Keycloak realm roles → ROLE_* */
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Object claim = jwt.getClaim("realm_access");

        Collection<Object> roles = Collections.emptyList();
        if (claim instanceof java.util.Map<?, ?> realmAccess) {
            Object rawRoles = realmAccess.getOrDefault("roles", Collections.emptyList());
            if (rawRoles instanceof Collection<?> rawList) {
                roles = new java.util.ArrayList<>(rawList);
            }
        }

        return roles.stream()
                .map(Object::toString)
                .map(r -> "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    /** Converter bean used by Spring-Security */
    @Bean
    JwtAuthenticationConverter keycloakRoleConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(this::extractRealmRoles);
        return converter;
    }

    /** Security rules */
    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/doctor/**").hasRole("Doctor")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(rs -> rs
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(keycloakRoleConverter())
                                .jwkSetUri("http://127.0.0.1:8080/realms/PMA/protocol/openid-connect/certs") ))
                .build();
    }
}