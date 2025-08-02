package com.example.backend.controller;

import com.example.backend.model.RegisterRequest;
import com.example.backend.model.requests.LoginRequest;
import com.example.backend.model.requests.LoginResponse;
import com.example.backend.service.UserAuthService;
import com.example.backend.service.UserRegistrationService;
import com.example.backend.service.FertilityRecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse tokenResponse = authService.login(request.username(), request.password());
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // unauthorized if wrong credentials
        }
    }
}
