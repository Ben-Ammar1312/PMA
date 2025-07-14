package com.example.backend.controller;
import com.example.backend.model.Couple;
import com.example.backend.model.FertilityRecord;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.FileStorageService;
import com.example.backend.service.SequenceGeneratorService;
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
    private final SequenceGeneratorService seqGen;

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
        long next = seqGen.getNextSequence("coupleCode");
        if (record.getCouple() == null) {
            record.setCouple(
                    Couple.builder()
                            .malePartner(   record.getMalePartner()   )
                            .femalePartner( record.getFemalePartner() )
                            .code( String.valueOf(next) )
                            .build()
            );
        }
        else {
            record.getCouple().setCode( String.valueOf(next) );
        }
        FertilityRecord saved = fertilityRecordService.addFertilityRecord(record);
        String coupleCode = saved.getCouple().getCode();

        // 2) store each of the four single uploads if present
        if (bilanHormonalFile        != null) fileStorageService.store(bilanHormonalFile,        coupleCode);
        if (echographiePelvienneFile != null) fileStorageService.store(echographiePelvienneFile, coupleCode);
        if (hsgFile                  != null) fileStorageService.store(hsgFile,                  coupleCode);
        if (spermogrammeFile         != null) fileStorageService.store(spermogrammeFile,         coupleCode);

        // 3) store the rest
        if (autreDocumentFiles != null) {
            for (MultipartFile f : autreDocumentFiles) {
                fileStorageService.store(f, coupleCode);
            }
        }
    }
}