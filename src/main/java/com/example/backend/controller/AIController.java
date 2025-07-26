package com.example.backend.controller;

import com.example.backend.service.AIIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AIController {

    private final AIIntegrationService aiIntegrationService;


    // Trigger AI summarization and save the summary to FertilityRecord


    @Operation(summary = "Generate patient summary and save in patient's record")
    @PostMapping("/summarize/{patientId}")
    public ResponseEntity<Map<String, Object>> summarizeAndSave(@PathVariable String patientId) {
            Map<String, Object> summary = aiIntegrationService.generateSummary(patientId);
            return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Find patient json file path")
    @GetMapping("/{patientId}/file-path")
    public ResponseEntity<String> getPatientFilePath(@PathVariable String patientId) {
        String filePath = aiIntegrationService.findPatientFilePath(patientId);
            return ResponseEntity.ok(filePath);
    }
}
