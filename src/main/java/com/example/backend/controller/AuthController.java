package com.example.backend.controller;

import com.example.backend.model.RegisterRequest;
import com.example.backend.service.UserRegistrationService;
import com.example.backend.service.FertilityRecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRegistrationService registrationService;
    private final FertilityRecordService fertilityRecordService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        String userId = registrationService.register(request);
        fertilityRecordService.createRecordForUser(userId);
    }
}
