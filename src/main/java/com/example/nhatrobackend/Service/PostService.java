package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.PostResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<PostResponseDTO> getAllPosts(Pageable pageable);
}
