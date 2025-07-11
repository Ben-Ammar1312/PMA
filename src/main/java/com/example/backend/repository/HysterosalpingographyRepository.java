package com.example.backend.repository;

import com.example.backend.model.Hysterosalpingography;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HysterosalpingographyRepository extends MongoRepository<Hysterosalpingography, String> {

    List<Hysterosalpingography> findByRecordId(String recordId);
}
