package com.example.backend.controller;

import com.example.backend.dto.RegisterDto;
import com.example.backend.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final UserRegistrationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterDto dto) {
        service.register(dto);
    }
}