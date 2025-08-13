package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;

import java.time.Instant;
import java.util.Map;

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
    private Instant date;

    private Map<String, Measurement> parameters;
}
