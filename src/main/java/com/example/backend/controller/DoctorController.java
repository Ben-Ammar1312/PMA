package com.example.backend.controller;

import com.example.backend.model.FertilityRecord;
import com.example.backend.model.FertilityRecordDetails;
import com.example.backend.service.FertilityRecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/doctor")
public class DoctorController {
    final FertilityRecordService fertilityRecordService;


    @Operation(summary = "List all patient fertility records")
    @GetMapping("/patients")
    public java.util.List<FertilityRecord> getPatients() {
        return fertilityRecordService.getAllRecords();
    }

    @Operation(summary = "Get a patient's fertility record")
    @GetMapping("/patient/{id}")
    public FertilityRecordDetails getPatientRecord(@PathVariable String id) {
        return fertilityRecordService.getFullFertilityRecord(id);
    }


    @Operation(summary = "Get a patient's summary")
    @GetMapping("/patient/{id}/summary")
    public String getPatientSummary(@PathVariable String id) {
        return fertilityRecordService.getSummary(id);
    }
}
