package com.example.backend.repository;

import com.example.backend.model.HormonePanel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HormonePanelRepository extends MongoRepository<HormonePanel, String> {

    List<HormonePanel> findByRecordId(String recordId);
}
