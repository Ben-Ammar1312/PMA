package com.example.backend.repository;

import com.example.backend.model.HematologyPanel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HematologyPanelRepository extends MongoRepository<HematologyPanel, String> {
    List<HematologyPanel> findByRecordId(String recordId);
    void  deleteAllByRecordId(String recordId);
}
