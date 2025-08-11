package com.example.backend.model;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Lifestyle {
    private String tobacco;
    private String cigarettesPerDay;
    private String alcohol;
    private String alcoholUnitsPerWeek;
    private String alcoholType;
    private String alcoholClass;
    private String drugClass;
    private String drugs;
    private String drugFrequency;
    private String physicalActivity;
    private String bmi;
}
