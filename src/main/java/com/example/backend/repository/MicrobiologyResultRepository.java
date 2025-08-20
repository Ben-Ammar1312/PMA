package com.example.backend.repository;

import com.example.backend.model.MicrobiologyResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MicrobiologyResultRepository extends MongoRepository<MicrobiologyResult, String> {

   // List<MicrobiologyResult> findByRecordId(String recordId);

    List<MicrobiologyResult> findByRecordIdOrderByDateDesc(String recordId);;

    void deleteAllByRecordId(String recordId);

}
