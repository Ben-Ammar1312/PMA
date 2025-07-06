package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Aggregated view of a fertility record including all associated
 * time-series analyses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FertilityRecordDetails {

    private FertilityRecord record;
    private List<BacteriologyAnalysis> bacteriologyAnalyses;
    private List<HormonePanel> hormonePanels;
    private List<Hysterosalpingography> hysterosalpingographies;
    private List<PelvicUltrasound> pelvicUltrasounds;
    private List<Spermogram> spermograms;
}

