package com.example.backend.model;

import com.example.backend.model.enums.BacteriologyCategory;
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
 * Individual bacteriology analysis stored as a separate document to avoid
 * unbounded array growth on the main fertility record.
 */
@Document("bacteriology_analyses")
@TimeSeries(timeField = "date", metaField = "recordId")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BacteriologyAnalysis {

    @Id
    private String id;

    /** Reference to the owning fertility record. */
    @Indexed
    private String recordId;

    /** Date of the test. Indexed for quick range queries. */
    @Indexed
    private LocalDate date;

    private BacteriologyCategory category;
    private String result;
    private String culture;
}
