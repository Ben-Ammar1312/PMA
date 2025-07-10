package com.example.backend.repository;

import com.example.backend.model.RadiologyReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RadiologyReportRepository extends MongoRepository<RadiologyReport, String> {
    List<RadiologyReport> findByRecordId(String recordId);
}
