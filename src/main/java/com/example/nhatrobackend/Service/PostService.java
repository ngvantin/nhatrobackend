package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.DTO.RoomRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<PostResponseDTO> getAllPosts(Pageable pageable);
    PostDetailResponseDTO getPostById(String postUuid);
    Page<PostResponseDTO> filterPosts(RoomRequestDTO roomRequestDTO, Pageable pageable);
}
