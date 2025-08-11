package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;


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
    private String date;

    private String hematocritPct;
    private String hemoglobinGDl;
    private String platelets10e3UL;
    private String rbc10e6UL;
    private String wbcUL;
}
