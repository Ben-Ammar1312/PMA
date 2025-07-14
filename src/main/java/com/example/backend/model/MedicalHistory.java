package com.example.backend.model;
import lombok.*;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MedicalHistory {
    private String medical;
    private String surgical;
    private String gynecological;
    private String allergies;
    private String family;
}
