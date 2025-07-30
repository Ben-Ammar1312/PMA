package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;

import java.time.LocalDate;

@Document("hematology_panels")
@TimeSeries(timeField = "date", metaField = "recordId")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HematologyPanel {
    @Id
    private String id;

    @Indexed
    private String recordId;

    @Indexed
    private LocalDate date;

    private Double hematocritPct;
    private Double hemoglobinGDl;
    private Integer platelets10e3UL;
    private Double rbc10e6UL;
    private Integer wbcUL;
}
