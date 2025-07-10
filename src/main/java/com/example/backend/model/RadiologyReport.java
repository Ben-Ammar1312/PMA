package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("radiology_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyReport {
    @Id
    private String id;

    @Indexed
    private String recordId;

    @Indexed
    private LocalDate date;

    private String bodyPart;
    private String modality;
    private String findings;
    private String conclusion;
    private String fileId;
}
