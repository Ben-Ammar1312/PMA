import com.example.backend.config.SecurityConfig;
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
        SecurityConfig config = new SecurityConfig();
        JwtAuthenticationConverter converter = config.jwtAuthenticationConverter();
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
