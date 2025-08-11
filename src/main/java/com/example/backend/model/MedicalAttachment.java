package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("medical_attachments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalAttachment {

    @Id
    private String id;

    @Indexed
    private String recordId;

    private String uploadedAt;
    private String type;
    private String file;
    private String comment;
}
