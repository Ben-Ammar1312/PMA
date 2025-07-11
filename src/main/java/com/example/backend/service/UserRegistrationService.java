package com.example.backend.service;

import com.example.backend.model.RegisterRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final Keycloak keycloak;



    @Value("${keycloak.target-realm:PMA}")
    private String targetRealm;      // where users go

    public void register(RegisterRequest request) {

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setEnabled(true);

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.getPassword());
        cred.setTemporary(false);
        user.setCredentials(List.of(cred));

        Response resp = keycloak.realm(targetRealm)   // ← **PMA**
                .users()
                .create(user);

        if (resp.getStatus() >= 400) {
            throw new RuntimeException(
                    "Failed to create user in realm '" + targetRealm +
                            "': HTTP " + resp.getStatus());
        }
    }
}