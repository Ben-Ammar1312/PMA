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
    private java.time.Instant date;

    private Measurement ejaculateVolume;
    private Measurement concentration;
    private Measurement progressiveMotility;
    private Measurement nonProgressiveMotility;
    private Measurement normalMorphology;
    private Measurement vitality;
    private Measurement abstinenceDays;
    private Measurement immotile;
    private Measurement leucocytes;
    private Measurement ph;
    private Measurement totalCount;
    private Measurement roundCells;
    private Measurement viscosity;
    private String remarks;
    private String fileCytogramId;
    private Measurement multipleAnomalyIndex;
    private java.util.Map<String, Measurement> casa;
    private java.util.Map<String, Measurement> morphologyBreakdown;
    private SpermCulture spermCulture;
}
