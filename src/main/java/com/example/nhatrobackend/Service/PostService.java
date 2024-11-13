package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<PostResponseDTO> getAllPosts(Pageable pageable);
    PostDetailResponseDTO getPostById(String postUuid);
    Page<PostResponseDTO> filterPosts(RoomRequestDTO roomRequestDTO, Pageable pageable);
    UserDetailDTO getUserByPostUuid(String postUuid);
    PostDetailResponseDTO  createPost(PostRequestDTO postRequestDTO, String userUuid);
}
