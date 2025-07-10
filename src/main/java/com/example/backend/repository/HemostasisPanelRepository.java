package com.example.backend.repository;

import com.example.backend.model.HemostasisPanel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HemostasisPanelRepository extends MongoRepository<HemostasisPanel, String> {
    List<HemostasisPanel> findByRecordId(String recordId);
}
