package com.example.backend.model;
import lombok.*;
import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Pregnancy {
    private String outcome;    // e.g. "BIRTH", "MIScarriage", "IVF", ...
    private String method;     // delivery route or ART technique used
    private LocalDate date;
}