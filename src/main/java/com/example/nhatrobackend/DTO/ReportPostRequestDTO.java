package com.example.nhatrobackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostRequestDTO {
    private String reason;  // Lý do báo cáo
    private String details; // Chi tiết báo cáo
    private MultipartFile video; // Add video file field
    private List<MultipartFile> images; // Add list of image files field
}

