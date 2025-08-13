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
import java.util.Map;

/**
 * Microbiology result stored separately to avoid unbounded record growth.
 */
@Document("microbiology_results")
@TimeSeries(timeField = "date", metaField = "recordId")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MicrobiologyResult {

    @Id
    private String id;

    /** Reference to the owning fertility record. */
    @Indexed
    private String recordId;

    /** Date of the test. Indexed for quick range queries. */
    @Indexed
    private Instant date;

    private String sampleSite;
    private String testType;
    private String result;
    private String organism;

    private Map<String, Measurement> details;
}
