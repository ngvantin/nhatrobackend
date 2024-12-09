package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.FavoritePost;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritePostRepository extends JpaRepository<FavoritePost, Integer> {
    // Có thể thêm các phương thức custom nếu cần
    Optional<FavoritePost> findByUserAndPost(User user, Post post);
}

