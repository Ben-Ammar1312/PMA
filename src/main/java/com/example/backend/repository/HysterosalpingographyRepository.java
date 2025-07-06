package com.example.backend.repository;

import com.example.backend.model.Hysterosalpingography;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HysterosalpingographyRepository extends MongoRepository<Hysterosalpingography, String> {
}
