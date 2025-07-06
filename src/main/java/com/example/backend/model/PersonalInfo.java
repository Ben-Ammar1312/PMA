package com.example.backend.model;
import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonalInfo {
    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private String occupation;
    private String address;
    private String email;
    private String phone;
}
