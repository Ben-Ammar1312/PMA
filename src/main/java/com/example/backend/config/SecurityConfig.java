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
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity        // enables @PreAuthorize / @PostAuthorize, etc.
public class SecurityConfig {

    /* ----------------------------------------------------------------
     * 1.  Convert Keycloak realm roles → ROLE_* authorities
     * ---------------------------------------------------------------- */
    private static Collection<GrantedAuthority> realmRoleAuthorities(Jwt jwt) {

        /*
         * Keycloak puts realm roles in:
         *   "realm_access": { "roles": [ "Doctor", "offline_access", ... ] }
         */
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return java.util.List.of();       // no roles → empty authority list
        }

        @SuppressWarnings("unchecked")
        Collection<String> roles = (Collection<String>) realmAccess.getOrDefault("roles", java.util.List.of());

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    /* ----------------------------------------------------------------
     * 2.  Spring-Security converter bean
     * ---------------------------------------------------------------- */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(SecurityConfig::realmRoleAuthorities);
        return converter;
    }

    /* ----------------------------------------------------------------
     * 3.  Filter-chain
     * ---------------------------------------------------------------- */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            JwtAuthenticationConverter jwtConverter) throws Exception {

        return http
                /* — stateless REST — */
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /* — authorisation rules — */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/doctor/**").hasRole("Doctor")   // == authority ROLE_Doctor
                        .anyRequest().authenticated()
                )

                /* — JWT resource-server — */
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtConverter)
                        )
                )

                .build();
    }
}