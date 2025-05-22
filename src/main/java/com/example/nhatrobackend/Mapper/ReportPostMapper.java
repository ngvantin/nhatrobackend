package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.ReportPostAdminDTO;
import com.example.nhatrobackend.Entity.ReportPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    config = MapStructConfig.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = "spring"
)
public interface ReportPostMapper {
    @Mapping(source = "post.postId", target = "postId")
    ReportPostAdminDTO reportPostToReportPostAdminDTO(ReportPost reportPost);
}
