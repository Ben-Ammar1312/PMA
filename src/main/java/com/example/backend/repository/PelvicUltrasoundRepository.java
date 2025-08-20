package com.example.backend.repository;

import com.example.backend.model.PelvicUltrasound;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PelvicUltrasoundRepository extends MongoRepository<PelvicUltrasound, String> {

    //List<PelvicUltrasound> findByRecordId(String recordId);
    List<PelvicUltrasound> findByRecordIdOrderByDateDesc(String recordId);;

    void deleteAllByRecordId(String recordId);
}
