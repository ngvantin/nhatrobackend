package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<PostResponseDTO> getAllPosts(Pageable pageable);
    PostDetailResponseDTO getPostById(String postUuid);
//    Page<PostResponseDTO> filterPosts(RoomRequestDTO roomRequestDTO, Pageable pageable);
    Page<PostResponseDTO> filterPosts(Double minPrice, Double maxPrice, Double minArea, Double maxArea, FurnitureStatus furnitureStatus, String city, String district, String ward, Pageable pageable);
    UserDetailDTO getUserByPostUuid(String postUuid);
    PostDetailResponseDTO  createPost(PostRequestDTO postRequestDTO, String userUuid);
    PostRequestDTO getPostForEdit(String postUuid, String userUuid);
    PostDetailResponseDTO updatePost( String postUuid, PostRequestDTO postRequestDTO, String userUuid);
    void deletePost( String postUuid, String userUuid);

    Post getPostByUuid(String postUuid);

    Page<PostResponseDTO> getPostsByStatusAndUser(PostStatus status, String userUuid, Pageable pageable);
    Page<PostResponseDTO> getFavoritePostsByUser(String userUuid, Pageable pageable);
    PostDetailResponseDTO approvePost(String postUuid);
    PostDetailResponseDTO rejectPost(String postUuid);
    Page<PostResponseDTO> searchPostsByKeyword(String keyword, Pageable pageable);
    Page<PostAdminDTO> getPostsForAdmin(PostStatus status, Pageable pageable);


}
