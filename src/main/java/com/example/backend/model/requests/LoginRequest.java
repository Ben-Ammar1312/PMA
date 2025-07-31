package com.example.backend.model.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

