package com.example.backend.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Bean
    public Keycloak keycloak(
            @Value("${keycloak.server-url:http://localhost:8080}") String serverUrl,
            @Value("${keycloak.realm:master}") String realm,
            @Value("${keycloak.client-id:admin-cli}") String clientId,
            @Value("${keycloak.username:admin}") String username,
            @Value("${keycloak.password:admin}") String password
    ) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(username)
                .password(password)
                .build();
    }
}
