package com.example.backend.model;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PelvicUltrasound {
    private LocalDate date;
    private String report;
    private AntralFollicles antralFollicles;
    private List<String> files; // GridFS ids
}