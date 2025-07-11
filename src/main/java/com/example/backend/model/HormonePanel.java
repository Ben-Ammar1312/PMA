package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;

import java.time.LocalDate;

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
    private LocalDate date;

    private Double fsh;
    private Double lh;
    private Double estradiol;
    private Double amh;
    private Double ft3;
    private Double ft4;
    private Double insulin;
    private Integer progesterone;
    private Double prolactin;
    private Double testosterone;
    private Double tsh;
    private String fileId; // GridFS id
}
