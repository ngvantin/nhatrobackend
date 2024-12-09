package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.FavoritePost;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Responsitory.FavoritePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FavoritePostServiceImpl implements FavoritePostService {
    private final FavoritePostRepository favoritePostRepository;
    private final UserService userService;
    private final PostService postService;


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
}


