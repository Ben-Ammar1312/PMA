package com.example.backend.repository;

import com.example.backend.model.FertilityRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface FertilityRecordRepository
        extends MongoRepository<FertilityRecord, String> {

    List<FertilityRecord> findAllByMalePartner_PersonalInfo_FirstNameIsNotNull();

    FertilityRecord findFertilityRecordById(String id);

}
