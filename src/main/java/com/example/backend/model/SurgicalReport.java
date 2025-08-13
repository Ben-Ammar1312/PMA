package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("surgical_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurgicalReport {
    @Id
    private String id;

    @Indexed
    private String recordId;

    @Indexed
    private Instant date;

    private Measurement procedure;
    private Measurement indication;
    private Measurement findings;
    private Measurement postOpPlan;
}
