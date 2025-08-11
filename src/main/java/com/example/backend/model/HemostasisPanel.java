package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;


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
    private String date;

    private String antithrombinIII;
    private String apcResistanceRatio;
    private String dahlbackFactor;
    private String proteinC;
    private String proteinS;
}
