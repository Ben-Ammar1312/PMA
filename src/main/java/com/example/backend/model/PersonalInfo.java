package com.example.backend.model;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonalInfo {
    private String lastName;
    private String firstName;
    private String birthDate;
    private String occupation;
    private String address;
    private String email;
    private String phone;
    private String maritalStatus;
    private String age;
}
