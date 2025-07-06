package com.example.backend.repository;

import com.example.backend.model.BacteriologyAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BacteriologyAnalysisRepository extends MongoRepository<BacteriologyAnalysis, String> {
}
