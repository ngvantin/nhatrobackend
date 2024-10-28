package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
import com.example.nhatrobackend.DTO.PostResponseDTO;
import com.example.nhatrobackend.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponseDTO> posts = postService.getAllPosts(pageable);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponseDTO> getPostById(@PathVariable Integer postId) {
        PostDetailResponseDTO postDetailResponseDTO = postService.getPostById(postId);
        return ResponseEntity.ok(postDetailResponseDTO);
    }

}
