package com.example.backend.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
@Document("fertility_records")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FertilityRecord {

    @Id
    private String id;

    private Couple couple;
    private Assessments assessments;
    private List<Treatment> treatments;

    private Boolean priorPma;
    private List<String> priorTechniques;
    private Integer currentChildren;
    private Integer childrenFromOtherUnion;
    private Instant created;
    private Instant updated;
    private Integer version;
}
