package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.Entity.PostImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = MapStructConfig.class)
public interface PostImageMapper {

    // Ánh xạ PostImage sang imageUrl (String)
    @Mapping(target = ".", source = "imageUrl")
    String toDto(PostImage postImage);

    // Ánh xạ List<PostImage> sang List<String> (List các URL ảnh)
    default List<String> toDtoList(List<PostImage> postImages) {
        if (postImages == null) {
            return Collections.emptyList();
        }
        return postImages.stream()
                .map(this::toDto)  // Sử dụng toDto để lấy URL
                .collect(Collectors.toList());
    }
}

