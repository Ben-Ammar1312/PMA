package com.example.backend.controller;

import com.example.backend.model.FertilityRecord;
import com.example.backend.model.Partner;
import com.example.backend.service.AIIntegrationService;
import com.example.backend.service.FileStorageService;
import com.example.backend.service.FertilityRecordService;
import com.example.backend.service.MailService;
import com.example.backend.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/secretary")
@RequiredArgsConstructor
public class SecretaryController {

    private final UserRegistrationService registrationService;
    private final FertilityRecordService fertilityRecordService;
    private final FileStorageService fileStorageService;
    private final AIIntegrationService aiIntegrationService;
    private final MailService mailService;

    @PostMapping(value = "/patient", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addPatient(
            @RequestPart("femalePartner") Partner femalePartner,
            @RequestPart("malePartner") Partner malePartner,
            @RequestPart(value = "autreDocumentFiles", required = false) MultipartFile[] autreDocumentFiles
    ) {
        String email = femalePartner.getPersonalInfo().getEmail();
        String firstName = femalePartner.getPersonalInfo().getFirstName();
        String lastName = femalePartner.getPersonalInfo().getLastName();
        List<String> userInfo = registrationService.registerBySecretary(firstName, lastName, email);
        String userId = userInfo.get(0);

        FertilityRecord record = FertilityRecord.builder()
                .id(userId)
                .femalePartner(femalePartner)
                .malePartner(malePartner)
                .build();
        fertilityRecordService.addFertilityRecord(record);

        mailService.sendTemporaryPasswordEmail(email);

        if (autreDocumentFiles != null) {
            int counter = 1;
            for (MultipartFile f : autreDocumentFiles) {
                if (f != null && !f.isEmpty()) {
                    fileStorageService.store(f, userId, "autreDocument", counter++);
                }
            }
        }

        try {
            Path patientPath = fileStorageService.resolvePatientDir(userId);
            aiIntegrationService.processAndIndexComplementary(userId, patientPath.toString());
            aiIntegrationService.generateSummary(userId);
        } catch (Exception e) {
            log.error("Error during AI processing for user {}", userId, e);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
