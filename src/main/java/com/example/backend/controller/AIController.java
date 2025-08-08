package com.example.backend.controller;

import com.example.backend.model.FertilityRecord;
import com.example.backend.model.requests.SummaryResponse;
import com.example.backend.repository.FertilityRecordRepository;
import com.example.backend.service.AIIntegrationService;
import com.example.backend.service.FertilityRecordService;
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
    private final FertilityRecordService fertilityRecordService;
    private final FertilityRecordRepository fertilityRecordRepository;




    @Operation(summary = "Generate patient summaries and save paths in patient's record")
    @PostMapping("/summarize/{patientId}")
    public ResponseEntity<SummaryResponse> summarizeAndSave(@PathVariable String patientId) {
        SummaryResponse summary = aiIntegrationService.generateSummary(patientId);
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Find patient json file path")
    @GetMapping("/{patientId}/file-path")
    public ResponseEntity<String> getPatientFilePath(@PathVariable String patientId) {
        String filePath = aiIntegrationService.findPatientFilePath(patientId);
            return ResponseEntity.ok(filePath);
    }

}
