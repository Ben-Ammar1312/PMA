package com.example.backend.model;

import com.example.backend.model.enums.ProtocolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Treatment {

    private ProtocolType protocolType;
    private List<Medication> medications;
    private LocalDate startDate;
    private LocalDate endDate;
    private String cycleOutcome;
    private String comment;
}
