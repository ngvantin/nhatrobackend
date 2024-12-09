package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.FavoritePost;

public interface FavoritePostService {
    FavoritePost addFavoritePost(String userUuid, String postUuid);
    void removeFavoritePost(String userUuid, String postUuid);
}

