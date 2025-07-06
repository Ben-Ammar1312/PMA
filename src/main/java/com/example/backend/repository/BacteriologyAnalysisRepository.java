package com.example.backend.repository;

import com.example.backend.model.BacteriologyAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BacteriologyAnalysisRepository extends MongoRepository<BacteriologyAnalysis, String> {

    List<BacteriologyAnalysis> findByRecordId(String recordId);
}
