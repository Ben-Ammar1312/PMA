package com.example.backend.repository;

import com.example.backend.model.HormonePanel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HormonePanelRepository extends MongoRepository<HormonePanel, String> {
}
