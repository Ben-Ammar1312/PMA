package com.example.backend.service;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FertilityRecordService {
    final FertilityRecordRepository fertilityRecordRepository;
    final BacteriologyAnalysisRepository bacteriologyAnalysisRepository;
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
    public FertilityRecordDetails getFullFertilityRecord(String id) {
        FertilityRecord record = getFertilityRecord(id);

        return FertilityRecordDetails.builder()
                .record(record)
                .bacteriologyAnalyses(bacteriologyAnalysisRepository.findByRecordId(id))
                .hormonePanels(hormonePanelRepository.findByRecordId(id))
                .hysterosalpingographies(hysterosalpingographyRepository.findByRecordId(id))
                .pelvicUltrasounds(pelvicUltrasoundRepository.findByRecordId(id))
                .spermograms(spermogramRepository.findByRecordId(id))
                .medicalAttachments(medicalAttachmentRepository.findByRecordId(id))
                .build();
    }

}
