package com.example.nhatrobackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportPostRequestDTO {
    private String reason;  // Lý do báo cáo
    private String details; // Chi tiết báo cáo

}

