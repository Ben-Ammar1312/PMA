package com.example.backend.model;
import lombok.*;

import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MenstrualCycles {
    private String ageFirstPeriod;
    private String regular;
    private String cycleLength;
    private String periodLength;
    private String dysmenorrhea;
    private String symptoms;
    private String lastPeriodDate;
}
