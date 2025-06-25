package com.example.backend.model;
import lombok.*;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Assessments {

    private List<Spermogram> spermograms;
    private List<HormonePanel> hormonePanels;
    private List<BacteriologyAnalysis> bacteriologyAnalyses;
    private Imaging imaging;
}