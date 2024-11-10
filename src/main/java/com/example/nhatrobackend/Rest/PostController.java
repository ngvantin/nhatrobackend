package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponseDTO> posts = postService.getAllPosts(pageable);

        // Tạo ResponseWrapper với status, message và data trong Controller
        ResponseWrapper<Page<PostResponseDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài posts thành công",
                posts
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postUuid}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> getPostDetail(@PathVariable String postUuid) {
        PostDetailResponseDTO postDetail = postService.getPostById(postUuid);
        return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(postDetail)
                .message("Thông tin bài đăng")
                .build());
    }

    @PostMapping("/filter")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> filterPosts(
            @RequestBody RoomRequestDTO roomRequestDTO,
            Pageable pageable) {
        Page<PostResponseDTO> filteredPosts = postService.filterPosts(roomRequestDTO, pageable);

        return ResponseEntity.ok(
                ResponseWrapper.<Page<PostResponseDTO>>builder()
                        .status("success")
                        .message("Danh sách bài đăng đã được lọc.")
                        .data(filteredPosts)
                        .build()
        );
    }
    @GetMapping("/{postUuid}/user")
    public ResponseEntity<ResponseWrapper<UserDetailDTO>> getUserByPostUuid(@PathVariable String postUuid){
        UserDetailDTO  userDetailDTO = postService.getUserByPostUuid(postUuid);

        return ResponseEntity.ok(ResponseWrapper.<UserDetailDTO>builder()
                .status("success")
                .data(userDetailDTO)
                .message("Thông tin người dùng của bài đăng")
                .build());
    }

}
