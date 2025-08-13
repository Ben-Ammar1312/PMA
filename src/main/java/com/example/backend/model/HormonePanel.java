package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;

import java.time.Instant;

/**
 * Hormone panel stored separately to keep fertility records compact.
 */
@Document("hormone_panels")
@TimeSeries(timeField = "date", metaField = "recordId")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HormonePanel {

    @Id
    private String id;

    @Indexed
    private String recordId;

    /** Date of the blood test. */
    @Indexed
    private Instant date;

    private Measurement fsh;
    private Measurement lh;
    private Measurement estradiol;
    private Measurement amh;
    private Measurement ft3;
    private Measurement ft4;
    private Measurement insulin;
    private Measurement progesterone;
    private Measurement prolactin;
    private Measurement testosterone;
    private Measurement tsh;
}
