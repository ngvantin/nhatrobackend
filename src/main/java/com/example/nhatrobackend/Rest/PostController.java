package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;

import com.example.nhatrobackend.DTO.response.PostStatsResponse;
import com.example.nhatrobackend.DTO.response.SimilarPostResponse;
import com.example.nhatrobackend.Entity.Field.EventType;
import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.PostService;
import com.example.nhatrobackend.Service.UploadImageFileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final AuthenticationFacade authenticationFacade;
    private final UploadImageFileService uploadImageFileService;
    @GetMapping("/user/{userUuid}") // Thêm endpoint mới
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> getPostsByUserUuid(
            @PathVariable String userUuid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PostResponseDTO> posts = postService.getPostsByUserUuid(userUuid, pageable);

//        List<PostResponseDTO> posts = postService.getPostsByUserUuid(userUuid);

        return ResponseEntity.ok(ResponseWrapper.<Page<PostResponseDTO>>builder()
                .status("success")
                .data(posts)
                .message("Các bài đăng của người dùng.")
                .build());
    }

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

    @GetMapping("/{postUuid}/similar")
    public ResponseEntity<ResponseWrapper<List<SimilarPostResponse>>> getSimilarPosts(
            @PathVariable String postUuid) {
        List<SimilarPostResponse> similarPosts = postService.getSimilarPosts(postUuid);

        return ResponseEntity.ok(ResponseWrapper.<List<SimilarPostResponse>>builder()
                .status("success")
                .data(similarPosts)
                .message("Các bài đăng tương tự.")
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
            @RequestParam(required = false) String keyword,
            Pageable pageable) {

        Page<PostResponseDTO> filteredPosts = postService.filterPosts(
                minPrice, maxPrice, minArea, maxArea, furnitureStatus, city, district, ward,keyword, pageable);

        // Kiểm tra nếu không có bài viết nào được tìm thấy
        if (!filteredPosts.hasContent()) {
            return ResponseEntity.ok(
                    ResponseWrapper.<Page<PostResponseDTO>>builder()
                            .status("success")
                            .message("Không tìm thấy kết quả phù hợp.")
                            .data(Page.empty()) // Trả về Page trống
                            .build()
            );
        }

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
        // , HttpServletRequest request

        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
//        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Gọi service để tạo bài viết
        PostDetailResponseDTO postDetailResponseDTO = postService.createPost(postRequestDTO, userUuid);

        return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(postDetailResponseDTO)
                .message("Bài đăng đã được tạo thành công.")
                .build());
    }

    @GetMapping("/edit/{postUuid}")
    public ResponseEntity<ResponseWrapper<PostRequestDTO>> getPostForEdit(
            @PathVariable String postUuid, HttpServletRequest request
    ) {
//        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Lấy thông tin bài viết
        PostRequestDTO postRequestDTO = postService.getPostForEdit(postUuid, userUuid);

        return ResponseEntity.ok(ResponseWrapper.<PostRequestDTO>builder()
                .status("success")
                .data(postRequestDTO)
                .message("Lấy thông tin bài viết thành công.")
                .build());
    }


    @PutMapping("/update/{postUuid}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> updatePost(
            @PathVariable String postUuid,
            @RequestBody PostRequestDTO postRequestDTO, HttpServletRequest request
    ){
//        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
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
            @PathVariable String postUuid, HttpServletRequest request
    ){
//        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
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

        try {
            for (MultipartFile file : files) {
                // Sử dụng UploadImageFileService để tải lên Cloudinary
                String fileUrl = uploadImageFileService.uploadImage(file);

                // Thêm URL vào danh sách kết quả
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
            @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {

        //        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
//        String userUuid = authenticationFacade.getCurrentUserUuid();
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
            @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {

        //        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
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


    @GetMapping("/anonymous")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> getAnonymousPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {

        //        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Tạo Pageable từ các tham số page và size
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách các bài post với trạng thái APPROVED cho user
        Page<PostResponseDTO> posts = postService.getPostsByStatusAndUser(PostStatus.ANONYMOUS,userUuid, pageable);

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
            @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {

        //        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
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
            @RequestParam(defaultValue = "10") int size, HttpServletRequest request) {

                String userUuid = authenticationFacade.getCurrentUserUuid();
                log.info("userUuid :"+ userUuid);
        // Lấy userUuid từ JWT token trong cookie
//        String userUuid = authenticationFacade.getCurrentUserUuid();

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

    @PutMapping("/admin/approve/{postId}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> approvePost(@PathVariable int postId) {
        // Duyệt bài viết
        PostDetailResponseDTO postDetail = postService.approvePost(postId);
        return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(postDetail)
                .message("Thông tin bài đăng")
                .build());
    }

    // Phương thức từ chối bài viết
    @PutMapping("/admin/reject/{postId}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> rejectPost(@PathVariable int postId) {
        // Từ chối bài viết
        PostDetailResponseDTO postDetail = postService.rejectPost(postId);

        // Trả về response với mã HTTP 200 OK
        return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(postDetail)
                .message("Thông tin bài đăng đã bị từ chối")
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> searchPostsByKeyword(
            @RequestParam String keyword,
            Pageable pageable) {

        Page<PostResponseDTO> posts = postService.searchPostsByKeyword(keyword, pageable);

        return ResponseEntity.ok(
                ResponseWrapper.<Page<PostResponseDTO>>builder()
                        .status("success")
                        .message("Danh sách bài đăng tìm kiếm được.")
                        .data(posts)
                        .build()
        );
    }

    @GetMapping("/admin/approved")
    public ResponseEntity<ResponseWrapper<Page<PostAdminDTO>>> getApprovedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("f--------------------- controller");
        Pageable pageable = PageRequest.of(page, size);
        Page<PostAdminDTO> posts = postService.getPostsForAdmin(PostStatus.APPROVED, pageable);

        ResponseWrapper<Page<PostAdminDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài posts đã được phê duyệt thành công",
                posts
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/reported")
    public ResponseEntity<ResponseWrapper<Page<PostAdminDTO>>> getReportedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostAdminDTO> posts = postService.getAllReportedPosts(pageable);

        ResponseWrapper<Page<PostAdminDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài posts bị tố cáo",
                posts
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/admin/rejected")
    public ResponseEntity<ResponseWrapper<Page<PostAdminDTO>>> getRejectedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostAdminDTO> posts = postService.getPostsForAdmin(PostStatus.REJECTED, pageable);

        ResponseWrapper<Page<PostAdminDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài posts bị từ chối thành công",
                posts
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/pending")
    public ResponseEntity<ResponseWrapper<Page<PostAdminDTO>>> getPendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostAdminDTO> posts = postService.getPostsForAdmin(PostStatus.PENDING, pageable);

        ResponseWrapper<Page<PostAdminDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài posts đang chờ xét duyệt thành công",
                posts
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/locked")
    public ResponseEntity<ResponseWrapper<Page<PostAdminDTO>>> getLockedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("f--------------------- controller");
        Pageable pageable = PageRequest.of(page, size);
        Page<PostAdminDTO> posts = postService.getPostsForAdmin(PostStatus.LOCKED, pageable);

        ResponseWrapper<Page<PostAdminDTO>> response = new ResponseWrapper<>(
                "success",
                "Lấy danh sách bài posts đã được phê duyệt thành công",
                posts
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/detail/{postId}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> getPostDetail(@PathVariable int postId) {
        PostDetailResponseDTO postDetail = postService.getPostAdminById(postId);
        return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(postDetail)
                .message("Thông tin bài đăng")
                .build());
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<ResponseWrapper<PostStatsResponse>> getPostStatsByYear(@PathVariable int year) {
        PostStatsResponse stats = postService.getPostStatsByYear(year);
        return ResponseEntity.ok(ResponseWrapper.<PostStatsResponse>builder()
                .status("success")
                .data(stats)
                .message("Thống kê số lượng bài viết theo trạng thái trong năm.")
                .build());
    }

    @PutMapping("/anonymous/{postUuid}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> makePostAnonymous(@PathVariable String postUuid) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        PostDetailResponseDTO postDetail = postService.makePostAnonymous(postUuid, currentUserId);
        return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(postDetail)
                .message("Bài đăng đã được chuyển sang trạng thái ẩn danh.")
                .build());
    }

    @PutMapping("/unanonymous/{postUuid}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> makePostUnanonymous(@PathVariable String postUuid) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        PostDetailResponseDTO postDetail = postService.makePostUnanonymous(postUuid, currentUserId);
        return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                .status("success")
                .data(postDetail)
                .message("Bài đăng đã được chuyển về trạng thái hiển thị.")
                .build());
    }

    @PostMapping(value = "/create-with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> createPostWithImages(
            @ModelAttribute CreatePostWithImagesDTO dto) {
        try {
            String userUuid = authenticationFacade.getCurrentUserUuid();
            log.info("Starting post creation with images and video for user: {}", userUuid);
            
            // Upload post images
            List<String> imageUrls = new ArrayList<>();
            if (dto.getImages() != null && !dto.getImages().isEmpty()) {
                log.info("Uploading {} post images", dto.getImages().size());
                for (MultipartFile file : dto.getImages()) {
                    if (file != null && !file.isEmpty()) {
                        log.info("Attempting to upload post image: {}", file.getOriginalFilename());
                        String fileUrl = uploadImageFileService.uploadImage(file);
                        imageUrls.add(fileUrl);
                        log.info("Successfully uploaded post image: {}", fileUrl);
                    }
                }
            } else {
                log.warn("No post images provided");
            }

            // Upload video
            String videoUrl = null;
            if (dto.getVideo() != null && !dto.getVideo().isEmpty()) {
                log.info("Attempting to upload video: {}", dto.getVideo().getOriginalFilename());
                videoUrl = uploadImageFileService.uploadImage(dto.getVideo());
                log.info("Successfully uploaded video: {}", videoUrl);
            } else {
                log.warn("No video provided");
            }

            // Upload license images
            String licensePcccUrl = null;
            if (dto.getLicensePccc() != null && !dto.getLicensePccc().isEmpty()) {
                log.info("Attempting to upload PCCC license image: {}", dto.getLicensePccc().getOriginalFilename());
                licensePcccUrl = uploadImageFileService.uploadImage(dto.getLicensePccc());
                log.info("Successfully uploaded PCCC license image: {}", licensePcccUrl);
            } else {
                log.warn("No PCCC license image provided");
            }

            String licenseBusinessUrl = null;
            if (dto.getLicenseBusiness() != null && !dto.getLicenseBusiness().isEmpty()) {
                log.info("Attempting to upload business license image: {}", dto.getLicenseBusiness().getOriginalFilename());
                licenseBusinessUrl = uploadImageFileService.uploadImage(dto.getLicenseBusiness());
                log.info("Successfully uploaded business license image: {}", licenseBusinessUrl);
            } else {
                log.warn("No business license image provided");
            }

            // Create PostRequestDTO from the uploaded files and other data
            PostRequestDTO postRequestDTO = new PostRequestDTO();
            postRequestDTO.setPostImages(imageUrls);
            postRequestDTO.setVideoUrl(videoUrl);
            postRequestDTO.setTitle(dto.getTitle());
            postRequestDTO.setDescription(dto.getDescription());
            postRequestDTO.setDepositAmount(dto.getDepositAmount());
            postRequestDTO.setPrice(dto.getPrice());
            postRequestDTO.setArea(dto.getArea());
            postRequestDTO.setFurnitureStatus(dto.getFurnitureStatus());
            postRequestDTO.setNumberOfRooms(dto.getNumberOfRooms());
            postRequestDTO.setElectricityPrice(dto.getElectricityPrice());
            postRequestDTO.setWaterPrice(dto.getWaterPrice());
            postRequestDTO.setCity(dto.getCity());
            postRequestDTO.setDistrict(dto.getDistrict());
            postRequestDTO.setWard(dto.getWard());
            postRequestDTO.setStreet(dto.getStreet());
            postRequestDTO.setHouseNumber(dto.getHouseNumber());
            postRequestDTO.setLicensePcccUrl(licensePcccUrl);
            postRequestDTO.setLicenseBusinessUrl(licenseBusinessUrl);
            postRequestDTO.setAllowDeposit(dto.getAllowDeposit());

            log.info("Creating post with {} images, video URL: {}, PCCC URL: {}, Business License URL: {}", 
                    imageUrls.size(), videoUrl, licensePcccUrl, licenseBusinessUrl);

            // Create the post using existing service
            PostDetailResponseDTO postDetailResponseDTO = postService.createPost(postRequestDTO, userUuid);

            log.info("Successfully created post with ID: {}", postDetailResponseDTO.getPostId());

            return ResponseEntity.ok(ResponseWrapper.<PostDetailResponseDTO>builder()
                    .status("success")
                    .data(postDetailResponseDTO)
                    .message("Bài đăng đã được tạo thành công.")
                    .build());
        } catch (IOException e) {
            log.error("Error uploading files: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseWrapper.<PostDetailResponseDTO>builder()
                            .status("error")
                            .message("Lỗi khi upload file: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Error creating post: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseWrapper.<PostDetailResponseDTO>builder()
                            .status("error")
                            .message("Lỗi khi tạo bài đăng: " + e.getMessage())
                            .build());
        }
    }

}

