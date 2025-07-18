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

    public FertilityRecord addFertilityRecord(FertilityRecord fertilityRecord) {
        return fertilityRecordRepository.save(fertilityRecord);
    }

    public FertilityRecord getFertilityRecord(String id) {
        return fertilityRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fertility record not found"));
    }

    public String getSummary(String id){
        return fertilityRecordRepository.findById(id).get().getSummary();
    }

    /**
     * Retrieve a fertility record along with all associated time-series
     * assessments.
     */
    public FertilityRecordDetails getFullFertilityRecord(String recordId) {

        // ① Fetch the master document by its id
        FertilityRecord record = fertilityRecordRepository
                .findById(recordId)
                .orElseThrow(() -> new NotFoundException(
                        "Fertility record with id=" + recordId + " not found"));
        var rid = record.getId();   // keep using the _id to join child docs

        // ② Build the DTO
        return FertilityRecordDetails.builder()
                .record(record)
                .microbiologyResults     (microbiologyResultRepository     .findByRecordId(rid))
                .hormonePanels           (hormonePanelRepository           .findByRecordId(rid))
                .hysterosalpingographies (hysterosalpingographyRepository .findByRecordId(rid))
                .pelvicUltrasounds       (pelvicUltrasoundRepository       .findByRecordId(rid))
                .spermograms             (spermogramRepository             .findByRecordId(rid))
                .medicalAttachments      (medicalAttachmentRepository      .findByRecordId(rid))
                .build();
    }

    /** create an empty fertility record for a newly registered user */
    public FertilityRecord createRecordForUser(String userId) {
        FertilityRecord record = FertilityRecord.builder()
                .id(userId)
                .build();
        return fertilityRecordRepository.save(record);
    }

    /** return all fertility records */
    public java.util.List<FertilityRecord> getAllRecords() {
        return fertilityRecordRepository.findAll();
    }

}
