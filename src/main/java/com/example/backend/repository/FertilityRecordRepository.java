package com.example.backend.repository;

import com.example.backend.model.FertilityRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FertilityRecordRepository
        extends MongoRepository<FertilityRecord, String> {

    /* ---------- derived queries / custom ones ---------- */

    // Example: find all records where the female partner is older than 40
    List<FertilityRecord> findByCoupleFemalePartnerPersonalInfoBirthDateBefore(LocalDate dobLimit);

    // Example: records where result is 'PREGNANCY'
    List<FertilityRecord> findByTreatmentsCycleOutcomeEquals(String resultCycle);

    List<FertilityRecord> findAllByMalePartner_PersonalInfo_FirstNameIsNotNull();

}
