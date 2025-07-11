package com.example.backend.model;

import com.example.backend.model.enums.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

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

    private Instant uploadedAt;
    private AttachmentType type;
    private String file;
    private String comment;
}
