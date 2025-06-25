package com.example.backend.model;
import lombok.*;
import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Hysterosalpingography {
    private LocalDate date;
    private String report;
    private String fileId; // GridFS id
}