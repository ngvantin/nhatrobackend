package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostAdminDTO {
    private Integer reportId;
    private Integer postId;
    private String reason;
    private String details;
    private ReportStatus status;
    private LocalDateTime createdAt;
}
