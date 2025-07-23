package com.example.backend.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CorsProperties corsProps;

    public SecurityConfig(CorsProperties corsProps) {
        this.corsProps = corsProps;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(corsProps.getAllowedOrigins());
        cfg.setAllowedMethods(corsProps.getAllowedMethods());
        cfg.setAllowedHeaders(corsProps.getAllowedHeaders());
        cfg.setAllowCredentials(corsProps.isAllowCredentials());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    /**
     * Convert Keycloak's realm_access.roles → Spring Security ROLE_...
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realm = jwt.getClaim("realm_access");
            if (realm == null) return List.<GrantedAuthority>of();
            @SuppressWarnings("unchecked")
            var roles = (List<String>) realm.getOrDefault("roles", List.of());
            return roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toSet());
        });
        return conv;
    }

    /**
     * One and only SecurityFilterChain:
     * 1) Allow EVERY OPTIONS for CORS
     * 2) Allow anonymous POST /register
     * 3) /doctor/** requires ROLE_Doctor
     * 4) all others require a valid JWT
     */
    @Bean
    public SecurityFilterChain apiSecurity(HttpSecurity http,
                                           JwtAuthenticationConverter jwtConverter) throws Exception {
        http
                // stateless, no CSRF
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // enable CORS with our bean below
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui/index.html"     // this one
                        ).permitAll()
                        // 1) CORS preflight


                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2) signup
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers("/ai/**").hasRole("Doctor")


                        // 3) doctor endpoints
                        .requestMatchers("/doctor/**").hasRole("Doctor")

                        // 4) everything else needs authentication
                        .anyRequest().authenticated()
                )

                // JWT resource‐server support
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(j -> j.jwtAuthenticationConverter(jwtConverter))
                );

        return http.build();
    }

    /**
     * CORS configuration so that your Next.js front
     * at http://localhost:3000 can talk to this API.
     */

}