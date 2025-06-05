package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Entity.Field.ReportStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostDetailDTO {

    private Integer reportId;
    private String reason;
    private String details;
    private LocalDateTime createdAt;
    private String videoUrl;
    private List<String> reportImages;
}
