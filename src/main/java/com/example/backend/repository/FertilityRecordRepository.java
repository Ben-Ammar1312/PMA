package com.example.backend.repository;

import com.example.backend.model.FertilityRecord;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface FertilityRecordRepository
        extends MongoRepository<FertilityRecord, String> {

}
