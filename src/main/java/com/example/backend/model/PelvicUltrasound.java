package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;

import java.util.List;

@Document("pelvic_ultrasounds")
@TimeSeries(timeField = "date", metaField = "recordId")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PelvicUltrasound {

    @Id
    private String id;

    @Indexed
    private String recordId;

    @Indexed
    private String date;
    private String report;
    private AntralFollicles antralFollicles;
    private List<String> files; // GridFS ids
}
