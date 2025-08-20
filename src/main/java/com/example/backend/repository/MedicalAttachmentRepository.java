package com.example.backend.repository;

import com.example.backend.model.MedicalAttachment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MedicalAttachmentRepository extends MongoRepository<MedicalAttachment, String> {

    List<MedicalAttachment> findByRecordId(String recordId);

    void deleteAllByRecordId(String recordId);
}