package com.example.backend.service;

import com.example.backend.model.RegisterRequest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private Response response;

    @InjectMocks
    private UserRegistrationService registrationService;

    @BeforeEach
    void setup() {
        // Inject the targetRealm property manually
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



        when(keycloak.realm("PMA")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any())).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        java.net.URI loc = java.net.URI.create("http://id/123");
        when(response.getLocation()).thenReturn(loc);

        // Act
        String id = registrationService.register(request);

        // Assert
        verify(usersResource, times(1)).create(any());
        org.junit.jupiter.api.Assertions.assertEquals("123", id);
    }

    @Test
    void register_shouldThrowException_whenKeycloakReturnsError() {

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

        // Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> registrationService.register(request)
        );

        org.junit.jupiter.api.Assertions.assertTrue(
                exception.getMessage().contains("Failed to create user")
        );
    }
}
