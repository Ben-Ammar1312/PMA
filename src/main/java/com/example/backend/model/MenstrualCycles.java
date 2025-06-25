package com.example.backend.model;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MenstrualCycles {
    private Integer ageFirstPeriod;
    private Boolean regular;
    private Integer cycleLength;
    private Integer periodLength;
    private Boolean dysmenorrhea;
    private List<String> symptoms;
    private LocalDate lastPeriodDate;
}