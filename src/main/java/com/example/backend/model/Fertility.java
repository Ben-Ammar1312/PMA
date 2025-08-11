package com.example.backend.model;
import lombok.*;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Fertility {

    private String primaryInfertility;
    private String infertilityDuration;  // months
    private String intercourseFrequency;

    /** Previous pregnancies for that partner */
    private String previousPregnancies;

    /* Female-specific sub-object – optional for male partner */
    private MenstrualCycles menstrualCycles;

    private Contraception contraception;
}
