package com.example.backend.service;

import com.example.backend.model.RegisterRequest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock private Keycloak keycloak;
    @Mock private RealmResource realmResource;
    @Mock private UsersResource usersResource;
    @Mock private Response response;
    @Mock private UserResource userResource;

    @InjectMocks private UserRegistrationService registrationService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(registrationService, "targetRealm", "PMA");
    }

    @Test
    void register_shouldSucceed_whenKeycloakReturns201() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("pass123")
                .build();

        URI location = URI.create("http://keycloak/realms/PMA/users/123");

        when(keycloak.realm("PMA")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any())).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.getLocation()).thenReturn(location);
        when(usersResource.get("123")).thenReturn(userResource);
        doNothing().when(userResource).sendVerifyEmail();

        // Act
        String id = registrationService.register(request);

        // Assert
        assertEquals("123", id);
        verify(usersResource).create(any());
        verify(usersResource).get("123");
        verify(userResource).sendVerifyEmail();
    }

    @Test
    void register_shouldThrowException_whenKeycloakReturnsError() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("fail@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("failpass")
                .build();

        when(keycloak.realm("PMA")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any())).thenReturn(response);
        when(response.getStatus()).thenReturn(409); // Conflict

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> registrationService.register(request)
        );

        assertTrue(exception.getMessage().contains("Failed to create user"));
    }
}
