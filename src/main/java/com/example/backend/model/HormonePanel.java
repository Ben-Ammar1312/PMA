package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;


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
    private String date;

    private String fsh;
    private String lh;
    private String estradiol;
    private String amh;
    private String ft3;
    private String ft4;
    private String insulin;
    private String progesterone;
    private String prolactin;
    private String testosterone;
    private String tsh;
}
