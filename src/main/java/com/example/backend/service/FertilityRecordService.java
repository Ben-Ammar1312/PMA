package com.example.backend.service;

import com.example.backend.model.FertilityRecord;
import com.example.backend.repository.FertilityRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FertilityRecordService {
    final FertilityRecordRepository fertilityRecordRepository;

    public void addFertilityRecord(FertilityRecord fertilityRecord) {
        fertilityRecordRepository.save(fertilityRecord);
    }

    public FertilityRecord getFertilityRecord(String id) {
        return fertilityRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fertility record not found"));
    }

}
