package com.example.backend.model;
import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HormonePanel {
    private LocalDate date;
    private Double fsh;
    private Double lh;
    private Double estradiol;
    private Double amh;
    private Double prolactin;
    private Double tsh;
}