package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Treatment {

    private String protocolType;
    private List<Medication> medications;
    private String startDate;
    private String endDate;
    private String cycleOutcome;
    private String comment;
}
