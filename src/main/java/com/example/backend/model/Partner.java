package com.example.backend.model;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Partner {
    public Partner(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }
    private PersonalInfo personalInfo;
    private MedicalHistory medicalHistory;
    private Lifestyle lifestyle;
    private Fertility fertility;
}
