package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.ReportPostAdminDTO;
import com.example.nhatrobackend.Entity.ReportPost;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ReportPostMapper {
    ReportPostAdminDTO reportPostToReportPostAdminDTO(ReportPost reportPost);
}
