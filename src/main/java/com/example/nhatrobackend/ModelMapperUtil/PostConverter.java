package com.example.nhatrobackend.ModelMapperUtil;

import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
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

    // Phương thức chuyển đổi từ Post sang PostDetailResponseDTO
    public PostDetailResponseDTO convertToDetailDTO(Post post) {
        PostDetailResponseDTO postDetailResponseDTO = modelMapper.map(post, PostDetailResponseDTO.class);

        // Lấy thông tin từ mối quan hệ Room
        if (post.getRoom() != null) {
            postDetailResponseDTO.setPrice(post.getRoom().getPrice());
            postDetailResponseDTO.setArea(post.getRoom().getArea());
            postDetailResponseDTO.setCity(post.getRoom().getCity());
            postDetailResponseDTO.setDistrict(post.getRoom().getDistrict());
            postDetailResponseDTO.setWard(post.getRoom().getWard());
            postDetailResponseDTO.setStreet(post.getRoom().getStreet());
            postDetailResponseDTO.setHouseNumber(post.getRoom().getHouseNumber());
            postDetailResponseDTO.setNumberOfRooms(post.getRoom().getNumberOfRooms());
            postDetailResponseDTO.setElectricityPrice(post.getRoom().getElectricityPrice());
            postDetailResponseDTO.setWaterPrice(post.getRoom().getWaterPrice());
        }

        // Lấy danh sách hình ảnh từ mối quan hệ PostImage
        if (post.getPostImages() != null) {
            List<String> imageUrls = post.getPostImages().stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList());
            postDetailResponseDTO.setPostImages(imageUrls);
        }

//        // Lấy thông tin chi tiết từ bài viết
//        postDetailResponseDTO.setDescription(post.getDescription());
//        postDetailResponseDTO.setDepositAmount(post.getDepositAmount());
//        postDetailResponseDTO.setVideoUrl(post.getVideoUrl());


        return postDetailResponseDTO;
    }
}
