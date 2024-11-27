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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> creatPost(
            @RequestBody PostRequestDTO postRequestDTO,
            @RequestParam("userUuid") String userUuid){
        PostDetailResponseDTO postDetailResponseDTO = postService.createPost(postRequestDTO, userUuid);

        return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(postDetailResponseDTO)
                .message("Bài đăng đã được tạo thành công.")
                .build());

    }

    @PutMapping("/update/{postUuid}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> updatePost(
            @PathVariable String postUuid,
            @RequestBody PostRequestDTO postRequestDTO,
            @RequestParam("userUuid") String userUuid
    ){
        PostDetailResponseDTO  updatePost = postService.updatePost(postUuid,postRequestDTO,userUuid);
        return  ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(updatePost)
                .message("Cập nhật bài đăng thành công.")
                .build());
    }

    @DeleteMapping("/delete/{postUuid}")
    public ResponseEntity<ResponseWrapper<String>> deletePost(
            @PathVariable String postUuid,
            @RequestParam("userUuid") String userUuid
    ){
        postService.deletePost(postUuid,userUuid);
        return  ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message("Xóa bài đăng thành công.")
                .build());
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseWrapper<List<String>>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> fileUrls = new ArrayList<>();
        String uploadDir = "D:\\anh\\"; // Thư mục lưu ảnh

        try {
            for (MultipartFile file : files) {
                // Tạo tên file duy nhất
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                // Kiểm tra thư mục lưu ảnh, tạo nếu chưa tồn tại
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Lưu file vào thư mục server
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Thêm đường dẫn lưu file vào danh sách kết quả
                String fileUrl = uploadDir + fileName; // Đường dẫn thực tế
                fileUrls.add(fileUrl);
            }

            // Trả về phản hồi thành công
            return ResponseEntity.ok(ResponseWrapper.<List<String>>builder()
                    .status("success")
                    .message("Upload thành công " + fileUrls.size() + " file(s).")
                    .data(fileUrls)
                    .build());
        } catch (IOException e) {
            // Xử lý lỗi upload và trả về phản hồi lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseWrapper.<List<String>>builder()
                            .status("error")
                            .message("Lỗi khi upload file: " + e.getMessage())
                            .build());
        }
    }




}

