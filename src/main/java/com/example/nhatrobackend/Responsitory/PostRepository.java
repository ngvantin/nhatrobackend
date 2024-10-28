package com.example.nhatrobackend.Responsitory;

import com.example.nhatrobackend.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository  extends JpaRepository<Post, Integer> {
}
