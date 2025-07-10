package com.example.backend.repository;

import com.example.backend.model.BiochemistryPanel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BiochemistryPanelRepository extends MongoRepository<BiochemistryPanel, String> {
    List<BiochemistryPanel> findByRecordId(String recordId);
}
