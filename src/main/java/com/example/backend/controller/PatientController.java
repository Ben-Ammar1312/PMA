package com.example.backend.controller;

import com.example.backend.model.FertilityRecord;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final FertilityRecordService fertilityRecordService;
    private final FileStorageService fileStorageService;

    @PostMapping(value = "/record",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void submitRecord(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("record") @Valid FertilityRecord record,
            @RequestPart("files")  MultipartFile[] files
    ) throws IOException {
        // 1) save your record (with couple.code inside)
        FertilityRecord saved = fertilityRecordService.addFertilityRecord(record);

        // 2) now grab the code out of that saved record
        String coupleCode = saved.getCouple().getCode();

        // 3) write each file under uploads/<coupleCode>/
        for (MultipartFile f : files) {
            fileStorageService.store(f, coupleCode);
        }
    }}