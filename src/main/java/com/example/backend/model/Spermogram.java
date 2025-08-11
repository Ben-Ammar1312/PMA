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
 * Spermogram results stored in their own collection so that the fertility
 * record does not grow unbounded over time.
 */
@Document("spermograms")
@TimeSeries(timeField = "date", metaField = "recordId")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Spermogram {

    @Id
    private String id;

    /** Owning fertility record identifier. */
    @Indexed
    private String recordId;

    /** Date of the test. Indexed for range queries. */
    @Indexed
    private String date;

    private String ejaculateVolume;
    private String concentration;
    private String progressiveMotility;
    private String nonProgressiveMotility;
    private String normalMorphology;
    private String vitality;
    private String abstinenceDays;
    private String immotile;
    private String leucocytes;
    private String ph;
    private String totalCount;
    private String roundCells;
    private String viscosity;
    private String remarks;
    private String fileCytogramId;
    private String multipleAnomalyIndex;
    private java.util.Map<String, String> casa;
    private java.util.Map<String, String> morphologyBreakdown;
    private SpermCulture spermCulture;
}
