package com.example.nhatrobackend.ModelMapperUtil;

import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.PostImage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostConverter {
    @Autowired
    private final ModelMapper modelMapper;

    public PostResponseDTO convertToDTO(Post post) {
        PostResponseDTO postResponseDTO = modelMapper.map(post, PostResponseDTO.class);

        // Lấy thông tin từ mối quan hệ Room
        if (post.getRoom() != null) {
            postResponseDTO.setPrice(post.getRoom().getPrice());
            postResponseDTO.setArea(post.getRoom().getArea());
            postResponseDTO.setCity(post.getRoom().getCity());
            postResponseDTO.setDistrict(post.getRoom().getDistrict());
            postResponseDTO.setWard(post.getRoom().getWard());
        }

        // Lấy danh sách hình ảnh từ mối quan hệ PostImage
        if (post.getPostImages() != null) {
            List<String> imageUrls = post.getPostImages().stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList());
            postResponseDTO.setPostImages(imageUrls);
        }

        return postResponseDTO;
    }
}
