package com.example.backend.service;

import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


    public FertilityRecord addFertilityRecord(FertilityRecord fertilityRecord) {
        return fertilityRecordRepository.save(fertilityRecord);
    }

    public FertilityRecord getFertilityRecord(String id) {
        return fertilityRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fertility Record not found with id " + id));
    }

    public String getSummary(String id){
        return getFertilityRecord(id).getSummary();
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
                .microbiologyResults     (microbiologyResultRepository     .findByRecordId(rid))
                .hormonePanels           (hormonePanelRepository           .findByRecordId(rid))
                .hysterosalpingographies (hysterosalpingographyRepository .findByRecordId(rid))
                .pelvicUltrasounds       (pelvicUltrasoundRepository       .findByRecordId(rid))
                .spermograms             (spermogramRepository             .findByRecordId(rid))
                .medicalAttachments      (medicalAttachmentRepository      .findByRecordId(rid))
                .radiologyReports        (radiologyReportRepository         .findByRecordId(rid))
                .surgicalReports         (surgicalReportRepository           .findByRecordId(rid))
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


    /** return all fertility records */
    public java.util.List<FertilityRecord> getAllRecords() {

        return fertilityRecordRepository.findAllByMalePartner_PersonalInfo_FirstNameIsNotNull();
    }

}
