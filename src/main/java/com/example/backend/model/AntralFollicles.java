package com.example.backend.model;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AntralFollicles {
    private String rightOvary;
    private String leftOvary;
    @Field("total")          // keep same JSON field
    private String totalCount;
}
