package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.ReportPostAdminDTO;
import com.example.nhatrobackend.DTO.ReportPostRequestDTO;
import com.example.nhatrobackend.Entity.ReportPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportPostService {
    ReportPost createReportPost(ReportPostRequestDTO requestDTO, String postUuid, String userUuid);
    Page<ReportPostAdminDTO> getAllReportedPosts(Pageable pageable);
}
