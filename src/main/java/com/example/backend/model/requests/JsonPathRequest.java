package com.example.backend.model.requests;

// src/main/java/com/example/backend/dto/JsonPathRequest.java
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonPathRequest {
    private String patient_json_path;
}

