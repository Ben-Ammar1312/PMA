package com.example.backend.model;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Treatment {

    private String protocolType;
    private List<Medication> medications;
    private LocalDate startDate;
    private LocalDate endDate;
    private String cycleOutcome;
    private String comment;
}