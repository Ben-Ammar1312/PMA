package com.example.backend.service;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FertilityRecordService {
    final FertilityRecordRepository fertilityRecordRepository;
    final MicrobiologyResultRepository microbiologyResultRepository;
    final HormonePanelRepository hormonePanelRepository;
    final HysterosalpingographyRepository hysterosalpingographyRepository;
    final PelvicUltrasoundRepository pelvicUltrasoundRepository;
    final SpermogramRepository spermogramRepository;
    final MedicalAttachmentRepository medicalAttachmentRepository;
    final RadiologyReportRepository radiologyReportRepository;
    final SurgicalReportRepository surgicalReportRepository;
    private static final Logger log = LoggerFactory.getLogger(FertilityRecordService.class);



    public FertilityRecord addFertilityRecord(FertilityRecord fertilityRecord) {
        return fertilityRecordRepository.save(fertilityRecord);
    }

    public FertilityRecord getFertilityRecord(String id) {
        return fertilityRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fertility Record not found with id " + id));
    }

    public String getSummary(String id){
        return getFertilityRecord(id).getSummary1Path();
    }

    /**
     * Retrieve a fertility record along with all associated time-series
     * assessments.
     */
    public FertilityRecordDetails getFullFertilityRecord(String recordId) {

        // ① Fetch the master document by its id
        FertilityRecord record = getFertilityRecord(recordId);
        var rid = record.getId();   // keep using the _id to join child docs

        // ② Build the DTO
        return FertilityRecordDetails.builder()
                .record(record)
                .microbiologyResults     (microbiologyResultRepository     .findByRecordIdOrderByDateDesc(rid))
                .hormonePanels           (hormonePanelRepository           .findByRecordIdOrderByDateDesc(rid))
                .hysterosalpingographies (hysterosalpingographyRepository .findByRecordIdOrderByDateDesc(rid))
                .pelvicUltrasounds       (pelvicUltrasoundRepository       .findByRecordIdOrderByDateDesc(rid))
                .spermograms             (spermogramRepository             .findByRecordIdOrderByDateDesc(rid))
                .medicalAttachments      (medicalAttachmentRepository      .findByRecordId(rid))
                .radiologyReports        (radiologyReportRepository         .findByRecordIdOrderByDateDesc(rid))
                .surgicalReports         (surgicalReportRepository           .findByRecordIdOrderByDateDesc(rid))
                .build();
    }

    /** create an empty fertility record for a newly registered user */
    public FertilityRecord createRecordForUser(List<String> userData) {
        if (userData == null || userData.size() < 4) {
            throw new IllegalArgumentException("User data list must contain at least 4 elements: userId, firstName, lastName, email");
        }

        String userId = userData.get(0);
        String firstName = userData.get(1);
        String lastName = userData.get(2);
        String email = userData.get(3);

        FertilityRecord record = FertilityRecord.builder()
                .id(userId)
                .femalePartner(
                        Partner.builder()
                                .personalInfo(
                                        PersonalInfo.builder()
                                                .email(email)
                                                .firstName(firstName)
                                                .lastName(lastName)
                                                // set other personal info fields if needed
                                                .build()
                                )
                                // you can set medicalHistory, lifestyle, fertility here as well if needed
                                .build()
                )

                // you can set couple, malePartner, treatments, etc.
                .build();


        return fertilityRecordRepository.save(record);

    }

    public void deleteFertilityRecord(String recordId) {
        microbiologyResultRepository.deleteByRecordId(recordId);
        hormonePanelRepository.deleteByRecordId(recordId);
        hysterosalpingographyRepository.deleteByRecordId(recordId);
        pelvicUltrasoundRepository.deleteByRecordId(recordId);
        spermogramRepository.deleteByRecordId(recordId);
        medicalAttachmentRepository.deleteByRecordId(recordId);
        radiologyReportRepository.deleteByRecordId(recordId);
        surgicalReportRepository.deleteByRecordId(recordId);
        fertilityRecordRepository.deleteById(recordId);
    }


    /** return all fertility records */
    public java.util.List<FertilityRecord> getAllRecords() {

        return fertilityRecordRepository.findAllByMalePartner_PersonalInfo_FirstNameIsNotNull();
    }

    public ResponseEntity<JsonNode> getSummaryFromFile(String recordId) {
        FertilityRecord fertilityRecord = fertilityRecordRepository.findFertilityRecordById(recordId);
        log.info("Record: {}", fertilityRecord);
        if (fertilityRecord == null) {
            return ResponseEntity.notFound().build();
        }

        String filePath = fertilityRecord.getSummary1Path();
        log.info("Summary path: {}", filePath);
        if (filePath == null || filePath.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        File jsonFile = new File(filePath);
        if (!jsonFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonContent = objectMapper.readTree(jsonFile);
            return ResponseEntity.ok(jsonContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


}
