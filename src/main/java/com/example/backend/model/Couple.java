package com.example.backend.model;
import lombok.*;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Couple {
    private Partner malePartner;
    private Partner femalePartner;
}
