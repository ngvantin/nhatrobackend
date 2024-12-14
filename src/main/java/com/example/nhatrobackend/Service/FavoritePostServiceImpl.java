package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.Entity.FavoritePost;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.PostMapper;
import com.example.nhatrobackend.Responsitory.FavoritePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FavoritePostServiceImpl implements FavoritePostService {
    private final FavoritePostRepository favoritePostRepository;
    private final UserService userService;
    private final PostService postService;
    private final PostMapper postMapper;

    @Override
    public FavoritePost addFavoritePost(String userUuid, String postUuid) {
        // Lấy User và Post từ các service tương ứng
        User user = userService.getUserByUuid(userUuid);
        Post post = postService.getPostByUuid(postUuid);

        // Kiểm tra nếu FavoritePost đã tồn tại
        if (favoritePostRepository.findByUserAndPost(user, post).isPresent()) {
            throw new RuntimeException("This post is already in the favorite list");
        }

        // Tạo mới FavoritePost
        FavoritePost favoritePost = new FavoritePost();
        favoritePost.setUser(user);
        favoritePost.setPost(post);
        favoritePost.setCreatedAt(LocalDateTime.now());

        // Lưu vào cơ sở dữ liệu
        return favoritePostRepository.save(favoritePost);

    }

    @Override
    public void removeFavoritePost(String userUuid, String postUuid) {
        // Lấy User và Post từ các service tương ứng
        User user = userService.getUserByUuid(userUuid);
        Post post = postService.getPostByUuid(postUuid);

        // Tìm FavoritePost nếu tồn tại
        FavoritePost favoritePost = favoritePostRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new RuntimeException("Favorite post không tìm thấy tương ứng với user và post"));

        // Xóa FavoritePost
        favoritePostRepository.delete(favoritePost);
    }

    @Override
    public boolean isPostFavorited(String userUuid, String postUuid) {
        // Lấy User và Post từ các service tương ứng
        User user = userService.getUserByUuid(userUuid);
        Post post = postService.getPostByUuid(postUuid);

        // Kiểm tra trong cơ sở dữ liệu xem đã tồn tại FavoritePost hay chưa
        return favoritePostRepository.findByUserAndPost(user, post).isPresent();
    }


}


