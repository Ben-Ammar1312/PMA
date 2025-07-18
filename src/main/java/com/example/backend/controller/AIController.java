package com.example.backend.controller;

import com.example.backend.service.AIIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AIController {

    private final AIIntegrationService aiIntegrationService;

    @GetMapping("/patient-file-path/{patientId}")
    public String getPatientFilePath(@PathVariable String patientId) {
        String path = aiIntegrationService.findPatientFilePath(patientId);
        if (path == null) {
            return "Patient file not found for ID: " + patientId;
        }
        return path;
    }
}
