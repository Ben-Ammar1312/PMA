package com.example.backend.model;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Medication {
    private String name;
    private Double dose;
    private String unit;
    private String frequency;
}