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

    public String register(RegisterRequest request) {

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getEmail()); // use email as username
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmailVerified(false);
        user.setRequiredActions(List.of("VERIFY_EMAIL"));
        user.setEnabled(true);

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.getPassword());
        cred.setTemporary(false);

        user.setCredentials(List.of(cred));

        Response resp = keycloak.realm(targetRealm)
                .users()
                .create(user);

        if (resp.getStatus() >= 400) {
            throw new RuntimeException(
                    "Failed to create user in realm '" + targetRealm +
                            "': HTTP " + resp.getStatus());
        }

        // location header ends with the new user's id
        String userId = resp.getLocation().getPath()
                .substring(resp.getLocation().getPath().lastIndexOf('/') + 1);

    /* 3️⃣  Send the verification e-mail right away.
           (This is optional – if you skip it, Keycloak will still show
            the ‘Verify e-mail’ page next time the user tries to log in.) */
        keycloak.realm(targetRealm)
                .users()
                .get(userId)
                .sendVerifyEmail();           // ← does the POST …/send-verify-email call

        return userId;

}}