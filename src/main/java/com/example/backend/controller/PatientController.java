package com.example.backend.controller;
import com.example.backend.model.FertilityRecord;
import com.example.backend.model.PersonalInfo;
import com.example.backend.model.requests.SummaryResponse;
import com.example.backend.service.AIIntegrationService;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.FileStorageService;
import com.example.backend.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final FertilityRecordService fertilityRecordService;
    private final FileStorageService fileStorageService;
    private final UserAuthService authService;
    private final AIIntegrationService aiIntegrationService;


    @Operation(summary = "Submit or update the authenticated user's record")
    @PostMapping(
            value    = "/record",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> submitRecord(
            @AuthenticationPrincipal Jwt jwt,

            // your JSON payload
            @RequestPart("record") @Valid FertilityRecord record,

            // all file inputs are now arrays
            @RequestPart(value = "bilanHormonalFiles",        required = false) MultipartFile[] bilanHormonalFiles,
            @RequestPart(value = "echographiePelvienneFiles", required = false) MultipartFile[] echographiePelvienneFiles,
            @RequestPart(value = "hsgFiles",                  required = false) MultipartFile[] hsgFiles,
            @RequestPart(value = "spermogrammeFiles",         required = false) MultipartFile[] spermogrammeFiles,
            @RequestPart(value = "autreDocumentFiles",       required = false) MultipartFile[] autreDocumentFiles

    ){

        // 1) persist your record
        String userId = jwt.getSubject();
        record.setId(userId);

        // ✅ Get the existing record if it exists
        FertilityRecord existingRecord = fertilityRecordService.getFertilityRecord(userId);

        if (existingRecord != null && existingRecord.getFemalePartner() != null && existingRecord.getFemalePartner().getPersonalInfo() != null) {
            // preserve name/email from existing record
            record.getFemalePartner()
                    .setPersonalInfo(
                            PersonalInfo.builder()
                                    .firstName(existingRecord.getFemalePartner().getPersonalInfo().getFirstName())
                                    .lastName(existingRecord.getFemalePartner().getPersonalInfo().getLastName())
                                    .email(existingRecord.getFemalePartner().getPersonalInfo().getEmail())
                                    .build()
                    );
        }
        FertilityRecord saved = fertilityRecordService.addFertilityRecord(record);
        authService.markSubmitted(userId);

        // 2) store each array of uploads if present

        if (bilanHormonalFiles != null) {
            int counter = 1;
            for (MultipartFile f : bilanHormonalFiles) {
                if (f != null && !f.isEmpty()) {
                    fileStorageService.store(f, userId, "bilanHormonal", counter++);
                }
            }
        }

        if (echographiePelvienneFiles != null) {
            int counter = 1;
            for (MultipartFile f : echographiePelvienneFiles) {
                if (f != null && !f.isEmpty()) {
                    fileStorageService.store(f, userId, "echographiePelvienne", counter++);
                }
            }
        }

        if (hsgFiles != null) {
            int counter = 1;
            for (MultipartFile f : hsgFiles) {
                if (f != null && !f.isEmpty()) {
                    fileStorageService.store(f, userId, "hsgFile", counter++);
                }
            }
        }

        if (spermogrammeFiles != null) {
            int counter = 1;
            for (MultipartFile f : spermogrammeFiles) {
                if (f != null && !f.isEmpty()) {
                    fileStorageService.store(f, userId, "spermogrammeFile", counter++);
                }
            }
        }

        if (autreDocumentFiles != null) {
            int counter = 1;
            for (MultipartFile f : autreDocumentFiles) {
                if (f != null && !f.isEmpty()) {          // ✅ guard
                    fileStorageService.store(
                            f,
                            userId,
                            "autreDocument",
                            counter++                     // only increment when we actually stored something
                    );
                }
            }
        }
        try {
            // Adjust this to the actual folder/file-naming your FileStorageService uses
            Path patientPath = fileStorageService.resolvePatientDir(userId);

            // 1️⃣ Process and index
            Map<String, Object> result = aiIntegrationService.processAndIndex(userId, patientPath.toString());
            log.info("process-and-index response: {}", result);

            // 2️⃣ Generate summaries and save them in DB
            SummaryResponse summaryResponse = aiIntegrationService.generateSummary(userId);
            log.info("Summary generation response: {}", summaryResponse);

        } catch (Exception e) {
            log.error("Error during OCR/indexing or summarization for user {}", userId, e);
            throw new RuntimeException("Could not complete OCR/indexing or summarization", e);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
}

    @Operation(summary = "Upload complementary files for the authenticated user")
    @PostMapping(
            value    = "/complementary-files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> uploadComplementaryFiles(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("files") MultipartFile[] files
    ){
        String patientId = jwt.getSubject();
        if (files == null || files.length == 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        int start = fileStorageService.nextIndex(patientId, "complementaryFiles");
        for (MultipartFile f : files) {
            if (f != null && !f.isEmpty()) {
                fileStorageService.store(f, patientId, "complementaryFiles", start++);
            }
        }
    return ResponseEntity.status(HttpStatus.CREATED).build();
}

    @Operation(summary="get patient's partial record by id to check if record was fully submitted")
    @GetMapping("/me/{id}")
    public FertilityRecord getPatientRecord(@PathVariable String id){
        return fertilityRecordService.getFertilityRecord(id);
    }
}

