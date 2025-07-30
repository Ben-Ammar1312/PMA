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
    private LocalDate date;

    private Double ejaculateVolume;
    private Double concentration;
    private Double progressiveMotility;
    private Double nonProgressiveMotility;
    private Double normalMorphology;
    private Double vitality;
    private Integer abstinenceDays;
    private Double immotile;
    private Double leucocytes;
    private Double ph;
    private Double totalCount;
    private Double roundCells;
    private String viscosity;
    private String remarks;
    private String fileCytogramId;
    private Double multipleAnomalyIndex;
    private java.util.Map<String, Object> casa;
    private java.util.Map<String, Object> morphologyBreakdown;
    private SpermCulture spermCulture;
}
