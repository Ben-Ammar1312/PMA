package com.example.backend.model;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AntralFollicles {
    private Integer rightOvary;
    private Integer leftOvary;
    @Field("total")          // keep same JSON field
    private Integer totalCount;
}