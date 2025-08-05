package com.example.backend.service;

import com.example.backend.exception.UserRegistrationException;
import com.example.backend.model.RegisterRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final Keycloak keycloak;
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationService.class);



    @Value("${keycloak.target-realm:PMA}")
    private String targetRealm;      // where users go

    public List<String> register(RegisterRequest request) {
        System.out.println("INSIDE : " + request.getEmail());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getEmail()); // use email as username
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmailVerified(false);
        user.setRequiredActions(List.of("VERIFY_EMAIL"));
        user.setEnabled(true);
        Map<String, List<String>> attrs = new HashMap<>();
        attrs.put("submitted", Collections.singletonList("false"));
        user.setAttributes(attrs);

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(request.getPassword());
        cred.setTemporary(false);

        user.setCredentials(List.of(cred));
        
        try {
                Response resp = keycloak.realm(targetRealm)
                        .users()
                        .create(user);

                if (resp.getStatus() >= 400) {
                throw new UserRegistrationException(
                        "Failed to create user in realm '" + targetRealm +
                                "': HTTP " + resp.getStatus());
                }
                // Ensure we have a Location header to extract the user identifier
                if (resp.getLocation() == null) {
                        throw new UserRegistrationException(
                                "Registration succeeded but response did not contain a Location header");
                }

                // Extract user ID from response
                String userId = resp.getLocation().getPath()
                        .substring(resp.getLocation().getPath().lastIndexOf('/') + 1);

                // ✅ 1. Get the 'patient' role from realm
                RoleRepresentation patientRole = keycloak.realm(targetRealm)
                        .roles()
                        .get("Patient")
                        .toRepresentation();

                // ✅ 2. Assign the role to the user
                keycloak.realm(targetRealm)
                        .users()
                        .get(userId)
                        .roles()
                        .realmLevel()
                        .add(List.of(patientRole));

                // ✅ 3. Optionally send verification email
                keycloak.realm(targetRealm)
                        .users()
                        .get(userId)
                        .sendVerifyEmail();

                return List.of(userId, user.getFirstName(), user.getLastName(), user.getEmail());
        }catch (UserRegistrationException e) {
                log.error("Keycloak connection test failed", e);
                throw e;
        }catch (Exception e){
                log.error("Registration failed", e);
                throw new UserRegistrationException("Unexpected error during user registration " + e.getMessage(),e);
        }
        }


    public void sendResetPasswordEmail(String email) {
        try {
            List<UserRepresentation> users = keycloak.realm(targetRealm)
                    .users()
                    .search(email);

            if (users.isEmpty()) {
                throw new UserRegistrationException("No user found with email: " + email);
            }

            UserRepresentation user = users.get(0);

            keycloak.realm(targetRealm)
                    .users()
                    .get(user.getId())
                    .executeActionsEmail(List.of("UPDATE_PASSWORD"));

        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
            throw new UserRegistrationException("Failed to send reset password email: " + e.getMessage(), e);
        }
    }

}