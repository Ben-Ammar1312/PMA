package com.example.backend.service;

import com.example.backend.exception.UserRegistrationException;
import com.example.backend.model.RegisterRequest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.net.URI;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock private Keycloak keycloak;
    @Mock private RealmResource realmResource;
    @Mock private UsersResource usersResource;
    @Mock private RolesResource rolesResource;
    @Mock private RoleResource roleResource;
    @Mock private UserResource userResource;
    @Mock private Response response;
    @Mock private RoleScopeResource roleScopeResource;
    @Mock private RoleMappingResource roleMappingResource;

    @InjectMocks private UserRegistrationService registrationService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(registrationService, "targetRealm", "PMA");
    }

    @Test
    @Disabled()
    void register_shouldSucceed_whenKeycloakReturns201() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("pass123")
                .build();

        URI location = URI.create("http://keycloak/realms/PMA/users/123");
        RoleRepresentation patientRole = new RoleRepresentation("Patient", null, false);

        when(keycloak.realm("PMA")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(response.getLocation()).thenReturn(location);

        when(usersResource.get("123")).thenReturn(userResource);

        when(userResource.roles()).thenReturn(roleMappingResource);
        when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
        doNothing().when(roleScopeResource).add(List.of(patientRole));

        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get("Patient")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(patientRole);

        doNothing().when(userResource).sendVerifyEmail();

        // Act
        List<String> result = registrationService.register(request);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("123", result.get(0));       // userId
        assertEquals("John", result.get(1));      // firstName
        assertEquals("Doe", result.get(2));       // lastName
        assertEquals("test@example.com", result.get(3));  // email

        verify(usersResource).create(any(UserRepresentation.class));
        verify(usersResource, times(2)).get("123");
        verify(userResource).sendVerifyEmail();
        verify(roleScopeResource).add(List.of(patientRole));
    }

    @Test
    @Disabled()
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
        RuntimeException exception = assertThrows(UserRegistrationException.class,
                () -> registrationService.register(request)
        );

        assertTrue(exception.getMessage().contains("Failed to create user"));
    }
}
