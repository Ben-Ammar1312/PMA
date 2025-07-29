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

@Document("hemostasis_panels")
@TimeSeries(timeField = "date", metaField = "recordId")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HemostasisPanel {
    @Id
    private String id;

    @Indexed
    private String recordId;

    @Indexed
    private LocalDate date;

    private java.math.BigDecimal antithrombinIII;
    private java.math.BigDecimal apcResistanceRatio;
    private java.math.BigDecimal dahlbackFactor;
    private java.math.BigDecimal proteinC;
    private java.math.BigDecimal proteinS;
}
