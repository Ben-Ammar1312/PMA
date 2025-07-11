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

@Document("hysterosalpingographies")
@TimeSeries(timeField = "date", metaField = "recordId")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hysterosalpingography {

    @Id
    private String id;

    @Indexed
    private String recordId;

    @Indexed
    private LocalDate date;
    private String report;
    private String fileId; // GridFS id
}
