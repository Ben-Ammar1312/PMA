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

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final Keycloak keycloak;
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationService.class);



    @Value("${keycloak.target-realm:PMA}")
    private String targetRealm;      // where users go

    public List<String> register(RegisterRequest request) {

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
        }catch (UserRegistrationException ex) {
                log.error("Keycloak connection test failed", e);
                throw ex;
        }catch (Exception e){
                log.error("Registration failed", e);
                throw new UserRegistrationException("Unexpected error during user registration " + e.getMessage(),e);
        }
        }}