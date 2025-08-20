package com.example.backend.repository;

import com.example.backend.model.SurgicalReport;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SurgicalReportRepository extends MongoRepository<SurgicalReport, String> {
   // List<SurgicalReport> findByRecordId(String recordId);
    List<SurgicalReport> findByRecordIdOrderByDateDesc(String recordId);;

    void deleteByRecordId(String recordId);
}
