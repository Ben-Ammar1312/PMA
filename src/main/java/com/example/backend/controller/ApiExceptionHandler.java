package com.example.backend.controller;

import com.example.backend.exception.AIServiceException;
import com.example.backend.exception.FileStorageException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UserRegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(ResourceNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleFileError(FileStorageException ex) {
        return Map.of("error", "File operation failed : "+ ex.getMessage());
    }

    @ExceptionHandler(UserRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleKeycloak(UserRegistrationException ex) {
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(AIServiceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Map<String, String> handleAI(AIServiceException ex) {
        return Map.of("error", "AI summarization failed : "+ ex.getMessage());
    }
}
