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
public class DepositComplaintRequestDTO {
    private String reason;  // Lý do khiếu nại
//    private String details; // Chi tiết khiếu nại
    private MultipartFile video; // Video khiếu nại
    private List<MultipartFile> images; // Danh sách ảnh khiếu nại
} 