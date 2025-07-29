package com.example.backend.controller;
import com.example.backend.exception.FileStorageException;
import com.example.backend.model.FertilityRecord;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;


@Slf4j
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
    public ResponseEntity<Void> submitRecord(
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

    ){

        // 1) persist your record
        record.setId(jwt.getSubject());
        record.setSubmitted(true);
        FertilityRecord saved = fertilityRecordService.addFertilityRecord(record);
        String patientId = saved.getId();

        // 2) store each of the four single uploads if present


        if (bilanHormonalFile        != null) fileStorageService.store(bilanHormonalFile,        patientId,"bilanHormonal",null);
        if (echographiePelvienneFile != null) fileStorageService.store(echographiePelvienneFile, patientId,"echographiePelvienne",null);
        if (hsgFile                  != null) fileStorageService.store(hsgFile,                  patientId,"hsgFile",null);
        if (spermogrammeFile         != null) fileStorageService.store(spermogrammeFile,         patientId,"spermogrammeFile",null);

        // “Other” documents ➜ logical name fixed, counter increments
        if (autreDocumentFiles != null) {
            int counter = 1;
            for (MultipartFile f : autreDocumentFiles) {
                if (f != null && !f.isEmpty()) {          // ✅ guard
                    fileStorageService.store(
                            f,
                            patientId,
                            "autreDocument",
                            counter++                     // only increment when we actually stored something
                    );
                }
            }
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
    }}

