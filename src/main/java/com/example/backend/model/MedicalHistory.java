package com.example.backend.model;
import lombok.*;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MedicalHistory {
    private List<String> medical;
    private List<String> surgical;
    private List<String> gynecological;
    private List<String> allergies;
    private List<String> family;
}
