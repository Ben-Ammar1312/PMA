package com.example.backend.repository;

import com.example.backend.model.Spermogram;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpermogramRepository extends MongoRepository<Spermogram, String> {
}
