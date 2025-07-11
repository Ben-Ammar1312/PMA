package com.example.backend.model;
import lombok.*;




@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Couple {
    private String code;
    private Partner malePartner;
    private Partner femalePartner;
}
