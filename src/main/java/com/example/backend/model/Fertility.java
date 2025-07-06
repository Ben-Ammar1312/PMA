package com.example.backend.model;
import lombok.*;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Fertility {

    private Boolean primaryInfertility;
    private Integer infertilityDuration;  // months
    private String intercourseFrequency;

    /** Previous pregnancies for that partner */
    private List<Pregnancy> previousPregnancies;

    /* Female-specific sub-object – optional for male partner */
    private MenstrualCycles menstrualCycles;

    private Contraception contraception;
}
