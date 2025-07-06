package com.example.backend.controller;

import com.example.backend.model.FertilityRecord;
import com.example.backend.service.FertilityRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/doctor")
public class DoctorController {
    final FertilityRecordService fertilityRecordService;

    public DoctorController(FertilityRecordService fertilityRecordService) {
        this.fertilityRecordService = fertilityRecordService;
    }

    @GetMapping("/patient/{id}")
    public FertilityRecord getPatientRecord(@PathVariable String id) {
        return fertilityRecordService.getFertilityRecord(id);
    }
}
