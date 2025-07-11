package com.example.backend.controller;

import com.example.backend.model.FertilityRecord;
import com.example.backend.service.FertilityRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {
    private final FertilityRecordService fertilityRecordService;

    @PostMapping("/record")
    @ResponseStatus(HttpStatus.CREATED)
    public void submitRecord(@RequestBody @Valid FertilityRecord fertilityRecord) {
        fertilityRecordService.addFertilityRecord(fertilityRecord);
    }
}
