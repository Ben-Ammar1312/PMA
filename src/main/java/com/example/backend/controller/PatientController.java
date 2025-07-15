package com.example.backend.controller;
import com.example.backend.model.FertilityRecord;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Submit or update the authenticated user's record")
    @PostMapping(
            value    = "/record",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void submitRecord(
            @AuthenticationPrincipal Jwt jwt,

            // your JSON payload
            @RequestPart("record") @Valid FertilityRecord record,

            // the four single files
            @RequestPart(value = "bilanHormonalFile",        required = false) MultipartFile bilanHormonalFile,
            @RequestPart(value = "echographiePelvienneFile", required = false) MultipartFile echographiePelvienneFile,
            @RequestPart(value = "hsgFile",                  required = false) MultipartFile hsgFile,
            @RequestPart(value = "spermogrammeFile",         required = false) MultipartFile spermogrammeFile,

            // the array of “other” documents
            @RequestPart(value = "autreDocumentFiles",       required = false) MultipartFile[] autreDocumentFiles
    ) throws IOException {
        // 1) persist your record
        record.setId(jwt.getSubject());
        FertilityRecord saved = fertilityRecordService.addFertilityRecord(record);
        String patientId = saved.getId();

        // 2) store each of the four single uploads if present
        if (bilanHormonalFile        != null) fileStorageService.store(bilanHormonalFile,        patientId);
        if (echographiePelvienneFile != null) fileStorageService.store(echographiePelvienneFile, patientId);
        if (hsgFile                  != null) fileStorageService.store(hsgFile,                  patientId);
        if (spermogrammeFile         != null) fileStorageService.store(spermogrammeFile,         patientId);

        // 3) store the rest
        if (autreDocumentFiles != null) {
            for (MultipartFile f : autreDocumentFiles) {
                fileStorageService.store(f, patientId);
            }
        }
    }
}