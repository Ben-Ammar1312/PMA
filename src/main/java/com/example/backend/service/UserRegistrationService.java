package com.example.backend.service;

import com.example.backend.dto.RegisterDto;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    @Value("${kc.url}")   private String kcUrl;
    @Value("${kc.realm}") private String kcRealm;
    @Value("${kc.adminRealm}") private String kcAdminRealm;
    @Value("${kc.admin}") private String kcAdmin;   // svc-account
    @Value("${kc.pass}")  private String kcPass;


    private Keycloak kc() {
        return KeycloakBuilder.builder()
                .serverUrl(kcUrl)
                .realm(kcAdminRealm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username(kcAdmin)
                .password(kcPass)
                .build();
    }

    @Transactional
    public void register(RegisterDto dto) {
        // 1️⃣ create Keycloak user
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(dto.password());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(dto.email());
        user.setEmail(dto.email());
        user.setEnabled(true);
        user.setCredentials(List.of(cred));

        Response res = kc().realm(kcRealm).users().create(user);
        if (res.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL)
            throw new IllegalStateException("KC creation failed: " + res.getStatus());

        String kcId = UriBuilder.fromUri(res.getLocation())
                .build()
                .getPath()
                .replaceFirst(".*/", "");
        log.info("✅ Keycloak user created: id={}, email={}", kcId, dto.email());

    }
}