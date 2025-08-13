package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a measured value with optional unit and reference range.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Measurement {
    private String value;
    private String unit;
    private String referenceRange;
}
