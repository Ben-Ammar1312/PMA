package com.example.backend.controller;

import com.example.backend.model.RegisterRequest;
import com.example.backend.model.requests.LoginRequest;
import com.example.backend.model.requests.LoginResponse;
import com.example.backend.service.UserAuthService;
import com.example.backend.service.UserRegistrationService;
import com.example.backend.service.FertilityRecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRegistrationService registrationService;
    private final FertilityRecordService fertilityRecordService;
    private final UserAuthService authService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        List<String> userInfo = registrationService.register(request);
        fertilityRecordService.createRecordForUser(userInfo);
    }

    @Operation(summary = "Login to user account")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            AccessTokenResponse tokenResponse = authService.login(request.getUsername(), request.getPassword());

            return ResponseEntity.ok(new LoginResponse(
                    tokenResponse.getToken(),
                    tokenResponse.getExpiresIn(),
                    tokenResponse.getRefreshToken(),
                    tokenResponse.getRefreshExpiresIn(),
                    tokenResponse.getTokenType()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // unauthorized if wrong credentials
        }
    }

    @Operation(summary = "Reset user password")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        registrationService.sendResetPasswordEmail(email);
        return ResponseEntity.ok("Reset password email sent to: " + email);
    }
}
