package com.example.backend.model;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Lifestyle {
    private Boolean tobacco;
    private Integer cigarettesPerDay;
    private Boolean alcohol;
    private Integer alcoholUnitsPerWeek;
    private String alcoholType;
    private String drugClass;
    private String drugFrequency;
    private String physicalActivity;
    private Double bmi;
}
