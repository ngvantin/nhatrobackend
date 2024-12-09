package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.ReportPostRequestDTO;
import com.example.nhatrobackend.Entity.ReportPost;

public interface ReportPostService {
    ReportPost createReportPost(ReportPostRequestDTO requestDTO, String postUuid, String userUuid);
}
