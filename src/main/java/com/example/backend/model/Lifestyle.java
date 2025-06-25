package com.example.backend.model;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Lifestyle {
    private Boolean tobacco;   // true / false; or cigarettes‑per‑day if preferred
    private Boolean alcohol;   // idem
    private String drugs;      // free text or enum
    private Double bmi;        // Body‑mass index
}
