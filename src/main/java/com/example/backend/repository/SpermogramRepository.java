package com.example.backend.repository;

import com.example.backend.model.Spermogram;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpermogramRepository extends MongoRepository<Spermogram, String> {

    //List<Spermogram> findByRecordId(String recordId);
    List<Spermogram> findByRecordIdOrderByDateDesc(String recordId);;
}
