package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    @Value("${keycloak.target-realm:PMA}")
    private String targetRealm;


    @Value("${keycloak.server-url}")
    private String url;

    @Value("${keycloak.login-client}")
    private String loginClient;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final Keycloak keycloak;

    public AccessTokenResponse login(String username, String password) {
        System.out.println("URL : " + url);
        System.out.println("Login client : " + loginClient);
        System.out.println("Client secret : " + clientSecret);
        System.out.println("Target realm : " + targetRealm);
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(url) // Keycloak URL
                    .realm(targetRealm)
                    .clientId(loginClient)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(username)
                    .password(password)
                    .build();

            // ✅ Get the token object (contains access + refresh token)
            return keycloak.tokenManager().getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Invalid credentials", e);
        }
    }

    public void markSubmitted(String userId) {
        UserResource userRes = keycloak.realm(targetRealm).users().get(userId);
        UserRepresentation userRep = userRes.toRepresentation();

        Map<String, List<String>> attrs = userRep.getAttributes();
        if (attrs == null) {
            attrs = new HashMap<>();
        }
        attrs.put("submitted", Collections.singletonList("true"));
        userRep.setAttributes(attrs);

        userRes.update(userRep);
    }
}
