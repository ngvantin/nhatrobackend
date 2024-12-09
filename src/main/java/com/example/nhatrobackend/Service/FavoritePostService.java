package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.Entity.FavoritePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoritePostService {
    FavoritePost addFavoritePost(String userUuid, String postUuid);
    void removeFavoritePost(String userUuid, String postUuid);

}

