package com.example.backend.model;
import lombok.*;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Imaging {

    private List<Hysterosalpingography> hysterosalpingographies;
    private List<PelvicUltrasound> pelvicUltrasounds;
}