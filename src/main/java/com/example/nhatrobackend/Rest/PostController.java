package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
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
    private final AuthenticationFacade authenticationFacade;
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

    @GetMapping("/detail/{postUuid}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> getPostDetail(@PathVariable String postUuid) {
        PostDetailResponseDTO postDetail = postService.getPostById(postUuid);
        return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(postDetail)
                .message("Thông tin bài đăng")
                .build());
    }

    @GetMapping("/filter")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> filterPosts(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxArea,
            @RequestParam(required = false) FurnitureStatus furnitureStatus,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String ward,
            Pageable pageable) {

        Page<PostResponseDTO> filteredPosts = postService.filterPosts(
                minPrice, maxPrice, minArea, maxArea, furnitureStatus, city, district, ward, pageable);

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
        @RequestBody PostRequestDTO postRequestDTO) {

    // Lấy userUuid từ Bearer Token
    String userUuid = authenticationFacade.getCurrentUserUuid();

    // Gọi service để tạo bài viết
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
            @RequestBody PostRequestDTO postRequestDTO
    ){
        // Lấy userUuid từ Bearer Token
        String userUuid = authenticationFacade.getCurrentUserUuid();
        PostDetailResponseDTO  updatePost = postService.updatePost(postUuid,postRequestDTO,userUuid);
        return  ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(updatePost)
                .message("Cập nhật bài đăng thành công.")
                .build());
    }

    @DeleteMapping("/delete/{postUuid}")
    public ResponseEntity<ResponseWrapper<String>> deletePost(
            @PathVariable String postUuid
    ){
        // Lấy userUuid từ Bearer Token
        String userUuid = authenticationFacade.getCurrentUserUuid();
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

    @GetMapping("/approved")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> getApprovedPostsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Lấy userUuid từ Bearer Token
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Tạo Pageable từ các tham số page và size
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách các bài post với trạng thái APPROVED cho user
        Page<PostResponseDTO> posts = postService.getPostsByStatusAndUser(PostStatus.APPROVED,userUuid, pageable);

        // Tạo ResponseWrapper với status, message và dữ liệu
        ResponseWrapper<Page<PostResponseDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài posts thành công",
                posts
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rejected")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> getRejectedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Lấy userUuid từ Bearer Token
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Tạo Pageable từ các tham số page và size
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách các bài post với trạng thái APPROVED cho user
        Page<PostResponseDTO> posts = postService.getPostsByStatusAndUser(PostStatus.REJECTED,userUuid, pageable);

        // Tạo ResponseWrapper với status, message và dữ liệu
        ResponseWrapper<Page<PostResponseDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài posts bị từ chối thành công",
                posts
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> getPendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Lấy userUuid từ Bearer Token
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Tạo Pageable từ các tham số page và size
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách các bài post với trạng thái APPROVED cho user
        Page<PostResponseDTO> posts = postService.getPostsByStatusAndUser(PostStatus.PENDING,userUuid, pageable);

        // Tạo ResponseWrapper với status, message và dữ liệu
        ResponseWrapper<Page<PostResponseDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài posts đang chờ xét duyệt thành công",
                posts
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/favorites")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> getFavoritePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Lấy userUuid từ Bearer Token
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Tạo Pageable từ các tham số page và size
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách các bài post yêu thích của user
        Page<PostResponseDTO> posts = postService.getFavoritePostsByUser(userUuid, pageable);

        // Tạo ResponseWrapper với status, message và dữ liệu
        ResponseWrapper<Page<PostResponseDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài post yêu thích thành công",
                posts
        );

        return ResponseEntity.ok(response);
    }

}

