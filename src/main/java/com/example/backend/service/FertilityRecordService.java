package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void addFertilityRecord(FertilityRecord fertilityRecord) {
        fertilityRecordRepository.save(fertilityRecord);
    }

    public FertilityRecord getFertilityRecord(String id) {
        return fertilityRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fertility record not found"));
    }

    /**
     * Retrieve a fertility record along with all associated time-series
     * assessments.
     */
    public FertilityRecordDetails getFullFertilityRecord(String coupleCode) {

        // ① Fetch the master document via the new key
        FertilityRecord record = fertilityRecordRepository
                .findByCoupleCode(coupleCode)
                .orElseThrow(() -> new NotFoundException(
                        "Fertility record with couple.code=" + coupleCode + " not found"));

        var recordId = record.getId();   // keep using the _id to join child docs

        // ② Build the DTO
        return FertilityRecordDetails.builder()
                .record(record)
                .microbiologyResults     (microbiologyResultRepository     .findByRecordId(recordId))
                .hormonePanels           (hormonePanelRepository           .findByRecordId(recordId))
                .hysterosalpingographies (hysterosalpingographyRepository .findByRecordId(recordId))
                .pelvicUltrasounds       (pelvicUltrasoundRepository       .findByRecordId(recordId))
                .spermograms             (spermogramRepository             .findByRecordId(recordId))
                .medicalAttachments      (medicalAttachmentRepository      .findByRecordId(recordId))
                .build();
    }

}
