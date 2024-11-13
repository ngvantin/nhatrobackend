package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.PostImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = MapStructConfig.class)
public interface PostImageMapper {
    @Mapping(target = "post", ignore = true)
    default List<PostImage> toPostImage(List<String> imageUrls, Post post){
        return imageUrls.stream()
                .map(imageUrl -> new PostImage(imageUrl,post))
                .collect(Collectors.toList());
    }
}

