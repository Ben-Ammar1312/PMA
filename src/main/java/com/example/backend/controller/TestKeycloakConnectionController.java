package com.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestKeycloakConnectionController {

    private final Keycloak keycloak;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${keycloak.target-realm:PMA}")
    private String targetRealm;
    
    @Value("${keycloak.server-url}")
    private String serverUrl;

    @GetMapping("/keycloak-connection")
    public ResponseEntity<String> testKeycloakConnection() {
        try {
            // Test 1: Check if we can get realm info
            RealmRepresentation realm = keycloak.realms().realm(targetRealm).toRepresentation();
            
            // Test 2: Check if we can list users (this requires proper permissions)
            int userCount = keycloak.realm(targetRealm).users().count();
            
            return ResponseEntity.ok(String.format(
                "✅ Keycloak connection successful!\n" +
                "Realm: %s\n" +
                "Display Name: %s\n" +
                "User Count: %d",
                realm.getRealm(),
                realm.getDisplayName(),
                userCount
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                "❌ Keycloak connection failed!\n" +
                "Error: " + e.getMessage() + "\n" +
                "Target Realm: " + targetRealm + "\n" +
                "Server URL: " + serverUrl
            );
        }
    }
    
    @GetMapping("/keycloak-server-test")
    public ResponseEntity<String> testKeycloakServer() {
        try {
            String url = serverUrl + "/realms/" + targetRealm;
            String response = restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok("✅ Keycloak server is reachable!\nResponse: " + response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                "❌ Cannot reach Keycloak server!\n" +
                "Error: " + e.getMessage() + "\n" +
                "URL: " + serverUrl + "/realms/" + targetRealm
            );
        }
    }
} 