package com.example.backend.model;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Lifestyle {
    private String tobacco;
    private Integer cigarettesPerDay;
    private String alcohol;
    private Integer alcoholUnitsPerWeek;
    private String alcoholType;
    private String alcoholClass;
    private String drugClass;
    private String drugs;
    private String drugFrequency;
    private String physicalActivity;
    private Double bmi;
}
