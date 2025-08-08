package com.example.backend.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
@Document("fertility_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FertilityRecord {

    @Id
    private String id;

    private Couple couple;
    private Partner femalePartner;
    private Partner malePartner;
    private List<Treatment> treatments;

    /** short description filled by practitioners */
    private String summary1Path;
    private String summary2Path;

    private String priorPma;
    private List<String> priorTechniques;
    private String currentChildren;
    private String childrenFromOtherUnion;
    private Instant created;
    private Instant updated;
    private Integer version;

}
