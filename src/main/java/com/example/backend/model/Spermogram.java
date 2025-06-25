package com.example.backend.model;
import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Spermogram {
    private LocalDate date;
    private Double ejaculateVolume;
    private Double concentration;
    private Double progressiveMotility;
    private Double normalMorphology;
    private Double vitality;
    private SpermCulture spermCulture;
}