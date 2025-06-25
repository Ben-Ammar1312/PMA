package com.example.backend.model;
import lombok.*;
import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BacteriologyAnalysis {
    private LocalDate date;
    private String category;   // "vaginal" | "cervical" | "urine"
    private String result;
    private String culture;
}