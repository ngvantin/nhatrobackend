package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.response.NotificationResponse;
import com.example.nhatrobackend.DTO.response.PostStatsResponse;
import com.example.nhatrobackend.DTO.response.SimilarPostResponse;
import com.example.nhatrobackend.Entity.*;
import com.example.nhatrobackend.Entity.Field.EventType;
import com.example.nhatrobackend.Entity.Field.FurnitureStatus;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Entity.Field.Status;
import com.example.nhatrobackend.Entity.Notification;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.Room;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.PostImageMapper;
import com.example.nhatrobackend.Mapper.PostMapper;
import com.example.nhatrobackend.Mapper.RoomMapper;
import com.example.nhatrobackend.Mapper.UserMapper;
import com.example.nhatrobackend.Responsitory.FavoritePostRepository;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Responsitory.ReportPostRepository;
import com.example.nhatrobackend.Service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.nhatrobackend.Entity.Field.PostStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final RoomMapper roomMapper;
    private final UserService userService;
    private final RoomService roomService;
    private final PostImageMapper postImageMapper;
    private final FavoritePostRepository favoritePostRepository;
    private final ReportPostRepository reportPostRepository;
    private final NotificationService notificationService;
    private final MailService mailService;
    private final SearchInformationService searchInformationService;

    @Value("${spring.application.serverName}")
    private String serverName;
    @Override
    public Page<PostResponseDTO> getPostsByUserUuid(String userUuid, Pageable pageable) {
        Page<Post> postPage = postRepository.findByUser_UserUuidOrderByCreatedAtDesc(userUuid, pageable);
        return postPage.map(postMapper::toPostResponseDTO);
    }
    @Override
    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        // Lấy tất cả các Post từ cơ sở dữ liệu dưới dạng Page
//        Page<Post> postPage = postRepository.findAll(pageable);

        // Lấy danh sách bài viết theo trạng thái và phân trang từ repository
        Page<Post> postPage = postRepository.findByStatusOrderByCreatedAtDesc(PostStatus.APPROVED, pageable);
        // Sử dụng mapStruct để chuyển đổi từng Post thành PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }


    // Xem chi tiết bài viết
    @Override
    public PostDetailResponseDTO getPostById(String postUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);

        // Nếu tìm thấy Post, chuyển đổi sang PostDetailResponseDTO
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            return postMapper.toPostDetailResponseDTO(post);
        } else {
            throw new EntityNotFoundException("Post not found with ID: " + postUuid);
        }
    }

    @Override
    public Page<PostResponseDTO> filterPosts(
            Double minPrice,
            Double maxPrice,
            Double minArea,
            Double maxArea,
            FurnitureStatus furnitureStatus,
            String city,
            String district,
            String ward,
            String keyword,
            Pageable pageable) {

        // Gọi repository với các tham số trực tiếp
        Page<Post> postPage = postRepository.findPostsByRoomCriteria(
                minPrice,
                maxPrice,
                minArea,
                maxArea,
                furnitureStatus,
                city,
                district,
                ward,
                keyword,
                pageable);

        // Sử dụng mapStruct để chuyển đổi từng Post thành PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }

    @Override
    public Page<PostResponseDTO> searchRoomsFlexible(Double minPrice, Double maxPrice, Double minArea, Double maxArea, String city, String district, String ward, Pageable pageable) {
        
        // Chuẩn hóa địa chỉ
        String normalizedCity = normalizeAddress(city);
        String normalizedDistrict = normalizeAddress(district);
        String normalizedWard = normalizeAddress(ward);
        log.info("Normalized addresses - City: {}, District: {}, Ward: {}", 
                normalizedCity, normalizedDistrict, normalizedWard);
        // Tìm kiếm với các điều kiện linh hoạt
        Page<Post> postPage = postRepository.findPostsByRoomCriteria(
                minPrice,
                maxPrice,
                minArea,
                maxArea,
                null, // furnitureStatus
                normalizedCity,
                normalizedDistrict,
                normalizedWard,
                null, // keyword
                pageable
        );

        // Nếu không tìm thấy kết quả, thử tìm kiếm với điều kiện lỏng hơn
        if (!postPage.hasContent()) {
            log.info("No results found with exact match, trying with partial match");
            postPage = postRepository.findPostsByRoomCriteria(
                    minPrice,
                    maxPrice,
                    minArea,
                    maxArea,
                    null,
                    city != null ? "%" + city + "%" : null,
                    district != null ? "%" + district + "%" : null,
                    ward != null ? "%" + ward + "%" : null,
                    null,
                    pageable
            );
        }

        return postPage.map(postMapper::toPostResponseDTO);
    }

    private String normalizeAddress(String address) {
        if (address == null) return null;
        
        // Loại bỏ các ký tự đặc biệt và khoảng trắng thừa
        String normalized = address.trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[.,]", "")
                .toLowerCase();
        
        // Xử lý các trường hợp đặc biệt
        normalized = normalized
                .replace("tp.", "hồ chí minh")
                .replace("tp ", "hồ chí minh ")
                .replace("thành phố hồ chí minh", "hồ chí minh")
                .replace("thành phố ", "")
                .replace("quan ", "quận ")
                .replace("phuong ", "phường ")
                .replace("hcm", "hồ chí minh");

        // Xử lý số phường/quận
        normalized = normalized.replaceAll("phường\\s*(\\d+)", "phường $1");
        normalized = normalized.replaceAll("quận\\s*(\\d+)", "quận $1");
        
        return normalized;
    }

    @Override
    public UserDetailDTO getUserByPostUuid(String postUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            return userMapper.toUserDetailDTO(post.getUser());
        }
        else{
            throw new EntityNotFoundException("Không tìm thấy kết quả phù hợp.");
        }
    }

    @Override
    public PostDetailResponseDTO createPost(PostRequestDTO postRequestDTO, String userUuid) {
        User user = userService.findByUserUuid(userUuid);
        if(!userService.getApprovedUserByUuid(userUuid)){
            throw new IllegalArgumentException("Chưa đăng ký tài khoản Chủ cho thuê, không thể đăng bài.");
        }
        
        // Create and save room first
        Room room = roomMapper.toRoom(postRequestDTO);
        roomService.saveRoom(room);
        
        // Create post without images first
        Post post = new Post();
        post.setTitle(postRequestDTO.getTitle());
        post.setDescription(postRequestDTO.getDescription());
        post.setDepositAmount(postRequestDTO.getDepositAmount());
        post.setVideoUrl(postRequestDTO.getVideoUrl());
        post.setStatus(PENDING);
        post.setCreatedAt(LocalDateTime.now());
        post.setRoom(room);
        post.setUser(user);
        // Set allowDeposit
        if (postRequestDTO.getAllowDeposit() != null) {
            post.setAllowDeposit(postRequestDTO.getAllowDeposit());
        } else {
            post.setAllowDeposit(true);
        }
        
        // Save post first to get the ID
        Post savedPost = postRepository.save(post);
        
        // Now create and save post images with the saved post reference
        if (postRequestDTO.getPostImages() != null && !postRequestDTO.getPostImages().isEmpty()) {
            List<PostImage> postImages = postRequestDTO.getPostImages().stream()
                .map(imageUrl -> new PostImage(imageUrl, savedPost))
                .collect(Collectors.toList());
            savedPost.setPostImages(postImages);
            postRepository.save(savedPost);
        }
        
        return postMapper.toPostDetailResponseDTO(savedPost);
    }

    @Override
    public PostRequestDTO getPostForEdit(String postUuid, String userUuid) {
        // Lấy bài viết dựa trên postUuid
        Post post = postRepository.findByPostUuid(postUuid)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài viết."));

        // Kiểm tra quyền của người dùng
        User user = userService.findByUserUuid(userUuid);
        if (!post.getUser().equals(user)) {
            throw new IllegalArgumentException("Bạn không có quyền truy cập bài viết này.");
        }

        // Chuyển đổi Post thành PostRequestDTO
        PostRequestDTO postRequestDTO = postMapper.toPostRequestDTO(post);

        // Thêm thông tin room từ RoomService
        RoomDTO roomDTO = roomMapper.toRoomDTO(post.getRoom());
        postMapper.updatePostRequestFromRoomDTO(roomDTO, postRequestDTO);
        postRequestDTO.setPostImages(postMapper.mapImagesToUrls(post.getPostImages()));
        return postRequestDTO;
    }


    @Override
    @Transactional
    public PostDetailResponseDTO updatePost(String postUuid, PostRequestDTO postRequestDTO, String userUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);
        if (optionalPost.isEmpty()) {
            throw new EntityNotFoundException("Không tìm thấy bài viết.");
        }

        Post post = optionalPost.get();
        User user = userService.findByUserUuid(userUuid);
        if (!post.getUser().equals(user)) {
            throw new IllegalArgumentException("Bạn không có quyền cập nhật bài viết này.");
        }

        // **Cập nhật các trường của Post**
        postMapper.updatePostFromDTO(postRequestDTO, post); // Tận dụng mapper (cần bổ sung)

        // **Cập nhật Room**
        if (post.getRoom() != null) {
            roomMapper.updateRoomFromDTO(postRequestDTO, post.getRoom()); // Cập nhật thông tin Room
        }

        // **Cập nhật PostImage**
        if (postRequestDTO.getPostImages() != null) {
            List<PostImage> updatedImages = postImageMapper.toPostImage(postRequestDTO.getPostImages(), post);
            post.getPostImages().clear(); // Xóa ảnh cũ
            post.getPostImages().addAll(updatedImages); // Thêm ảnh mới
        }
        post.setStatus(PENDING);

        // **Lưu các thay đổi**
        Post updatedPost = postRepository.save(post);

        return postMapper.toPostDetailResponseDTO(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(String postUuid, String userUuid) {
        Optional<Post> optionalPost = postRepository.findByPostUuid(postUuid);
        if(optionalPost.isPresent()){
            Post post = optionalPost.get();
            User user = userService.findByUserUuid(userUuid);
            if(!post.getUser().equals(user)){
                throw new IllegalArgumentException("Bạn không có quyền cho bài viết này");
            }
            postRepository.delete(post);
        }
        else{
            throw new EntityNotFoundException("Không tìm thấy bài viết.");
        }
    }

    @Override
    public Post getPostByUuid(String postUuid) {
        return postRepository.findByPostUuid(postUuid)
                .orElseThrow(() -> new RuntimeException("Post not found with UUID: " + postUuid));
    }

    @Override
    public Page<PostResponseDTO> getPostsByStatusAndUser(PostStatus status, String userUuid, Pageable pageable) {
        // Lấy userId từ userUuid
        User user = userService.getUserByUuid(userUuid);
        Integer userId = user.getUserId(); // Lấy userId từ User

        // Lọc các bài post có trạng thái APPROVED và userId tương ứng
        Page<Post> postPage = postRepository.findByStatusAndUser_UserIdOrderByCreatedAtDesc(status, userId, pageable);

        // Chuyển đổi từ Post sang PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }

    @Override
    public Page<PostResponseDTO> getFavoritePostsByUser(String userUuid, Pageable pageable) {
        // Lấy danh sách FavoritePost của User
        Page<FavoritePost> favoritePostsPage = favoritePostRepository.findByUser_UserUuid(userUuid, pageable);

        // Lấy danh sách Post từ FavoritePost và chuyển đổi sang PostResponseDTO
        return favoritePostsPage.map(favoritePost -> postMapper.toPostResponseDTO(favoritePost.getPost()));
    }

    @Override
    public PostDetailResponseDTO approvePost(int postId) {
        // Tìm bài viết theo postId
        Optional<Post> optionalPost = postRepository.findById(postId);

        // Kiểm tra bài viết có tồn tại hay không
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Cập nhật trạng thái bài viết
            post.setStatus(PostStatus.APPROVED);
//            post.setUpdatedAt(LocalDateTime.now());

            // Lưu lại bài viết đã cập nhật
            Post savedPost = postRepository.save(post);

            User user = post.getUser();
            user.setPostCount(user.getPostCount()-1);
            userService.save(user);

            // Tạo và lưu notification vào database
            Notification notification = Notification.builder()
                    .title("Bài đăng đã được duyệt")
                    .content("Bài đăng " + post.getTitle() + " của bạn đã được phê duyệt")
                    .type(EventType.POST_APPROVED.name())
                    .userId(post.getUser().getUserId())
                    .postId(post.getPostId())
                    .redirectUrl("/posts/" + post.getPostId())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            // Lưu notification vào database
            Notification savedNotification = notificationService.save(notification);

            // Tạo NotificationResponse từ notification đã lưu
            NotificationResponse notificationResponse = NotificationResponse.builder()
                    .id(savedNotification.getId())
                    .title(savedNotification.getTitle())
                    .content(savedNotification.getContent())
                    .type(savedNotification.getType())
                    .userId(savedNotification.getUserId())
                    .postId(savedNotification.getPostId())
                    .createdAt(savedNotification.getCreatedAt())
                    .isRead(savedNotification.isRead())
                    .redirectUrl(savedNotification.getRedirectUrl())
                    .build();

            // Tạo và gửi notification event
            NotificationEvent event = NotificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .type(EventType.POST_APPROVED)
                    .notification(notificationResponse)
                    .timestamp(LocalDateTime.now())
                    .metadata(Map.of(
                            "postId", post.getPostId(),
                            "userId", post.getUser().getUserId()
                    ))
                    .priority(NotificationEvent.Priority.HIGH)
                    .status(Status.PENDING)
                    .build();

            // Gửi notification
            notificationService.sendNotification(event);

            // Log để debug
            log.info("Notification saved to database with ID: {}", savedNotification.getId());
            log.info("Notification event sent: {}", event);

            // Gửi email thông báo
            try {
                String postUrl = String.format("%s/posts/%d", serverName, post.getPostId());
                mailService.sendPostApprovedNotification(post.getUser().getEmail(), post.getTitle(), postUrl);
            } catch (Exception e) {
                log.error("Failed to send post rejection email", e);
                // Không throw exception vì đây không phải là lỗi nghiêm trọng
            }

            // Gửi thông báo cho người theo dõi
            notificationService.sendNewPostNotificationToFollowers(user.getUserId(), savedPost.getPostId(), savedPost.getTitle());
            List<String> followerEmails = userService.getFollowerEmails(user.getUserId());
            if(followerEmails != null && !followerEmails.isEmpty()) {
                mailService.sendNewPostNotificationToFollowers(followerEmails, user.getFullName(), post.getTitle(), String.format("%s/posts/%d", serverName, post.getPostId()));
            }

            // Gửi thông báo cho người dùng có tiêu chí tìm kiếm phù hợp
            Room room = post.getRoom();
            if (room != null) {
                List<String> matchingUserEmails = searchInformationService.findMatchingUserEmailsForRoom(room);
                if (matchingUserEmails != null && !matchingUserEmails.isEmpty()) {
                    String location = String.format("%s, %s, %s", room.getWard(), room.getDistrict(), room.getCity());
                    mailService.sendMatchingPostNotification(
                        matchingUserEmails,
                        post.getTitle(),
                        String.format("%s/posts/%d", serverName, post.getPostId()),
                        location
                    );

                    // Gửi notification cho từng người dùng phù hợp
                    for (String email : matchingUserEmails) {
                        User matchingUser = userService.getUserByEmail(email);
                        if (matchingUser != null) {
                            Notification matchingNotification = Notification.builder()
                                .title("Có phòng trọ phù hợp với yêu cầu của bạn")
                                .content(String.format("Phòng trọ \"%s\" tại %s phù hợp với yêu cầu tìm kiếm của bạn.", post.getTitle(), location))
                                .type(EventType.MATCHING_POST.name())
                                .userId(matchingUser.getUserId())
                                .postId(post.getPostId())
                                .redirectUrl(String.format("/posts/%d", post.getPostId()))
                                .isRead(false)
                                .createdAt(LocalDateTime.now())
                                .build();

                            Notification savedMatchingNotification = notificationService.save(matchingNotification);
                            NotificationResponse matchingNotificationResponse = NotificationResponse.builder()
                                .id(savedMatchingNotification.getId())
                                .title(savedMatchingNotification.getTitle())
                                .content(savedMatchingNotification.getContent())
                                .type(savedMatchingNotification.getType())
                                .userId(savedMatchingNotification.getUserId())
                                .postId(savedMatchingNotification.getPostId())
                                .createdAt(savedMatchingNotification.getCreatedAt())
                                .isRead(savedMatchingNotification.isRead())
                                .redirectUrl(savedMatchingNotification.getRedirectUrl())
                                .build();

                            NotificationEvent matchingEvent = NotificationEvent.builder()
                                .eventId(UUID.randomUUID().toString())
                                .type(EventType.MATCHING_POST)
                                .notification(matchingNotificationResponse)
                                .timestamp(LocalDateTime.now())
                                .metadata(Map.of(
                                    "postId", post.getPostId(),
                                    "userId", matchingUser.getUserId(),
                                    "location", location
                                ))
                                .priority(NotificationEvent.Priority.HIGH)
                                .status(Status.PENDING)
                                .build();

                            notificationService.sendNotification(matchingEvent);
                        }
                    }
                }
            }

            // Trả về response DTO
            return postMapper.toPostDetailResponseDTO(savedPost);
        } else {
            throw new EntityNotFoundException("Post not found with ID: " + postId);
        }
    }

    @Override
    // Phương thức từ chối bài viết
    public PostDetailResponseDTO rejectPost(int postId) {
        // Tìm bài viết theo postUuid
        Optional<Post> optionalPost = postRepository.findById(postId);

        // Kiểm tra bài viết có tồn tại hay không
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Cập nhật trạng thái bài viết thành REJECTED
            post.setStatus(PostStatus.REJECTED);
            post.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian sửa

            // Lưu lại bài viết đã cập nhật
            postRepository.save(post);


            // Tạo và lưu notification vào database
            Notification notification = Notification.builder()
                    .title("Bài đăng đã bị từ chối")
                    .content("Bài đăng " + post.getTitle() + " của bạn đã bị từ chối")
                    .type(EventType.POST_APPROVED.name())
                    .userId(post.getUser().getUserId())
                    .postId(post.getPostId())
                    .redirectUrl("/posts/" + post.getPostId())
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            // Lưu notification vào database
            Notification savedNotification = notificationService.save(notification);

            // Tạo NotificationResponse từ notification đã lưu
            NotificationResponse notificationResponse = NotificationResponse.builder()
                    .id(savedNotification.getId())
                    .title(savedNotification.getTitle())
                    .content(savedNotification.getContent())
                    .type(savedNotification.getType())
                    .userId(savedNotification.getUserId())
                    .postId(savedNotification.getPostId())
                    .createdAt(savedNotification.getCreatedAt())
                    .isRead(savedNotification.isRead())
                    .redirectUrl(savedNotification.getRedirectUrl())
                    .build();

            // Tạo và gửi notification event
            NotificationEvent event = NotificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .type(EventType.POST_APPROVED)
                    .notification(notificationResponse)
                    .timestamp(LocalDateTime.now())
                    .metadata(Map.of(
                            "postId", post.getPostId(),
                            "userId", post.getUser().getUserId()
                    ))
                    .priority(NotificationEvent.Priority.HIGH)
                    .status(Status.PENDING)
                    .build();

            // Gửi notification
            notificationService.sendNotification(event);

            // Log để debug
            log.info("Notification saved to database with ID: {}", savedNotification.getId());
            log.info("Notification event sent: {}", event);


            // Gửi email thông báo
            try {
                String postUrl = String.format("%s/posts/%d", serverName, post.getPostId());
                mailService.sendPostRejectedNotification(post.getUser().getEmail(), post.getTitle(), postUrl);
            } catch (Exception e) {
                log.error("Failed to send post rejection email", e);
                // Không throw exception vì đây không phải là lỗi nghiêm trọng
            }

            // Trả về response DTO
            return postMapper.toPostDetailResponseDTO(post);  // Chuyển đổi thành DTO nếu cần
        } else {
            throw new EntityNotFoundException("Post not found with ID: " + postId);
        }
    }

    @Override
    public Page<PostResponseDTO> searchPostsByKeyword(String keyword, Pageable pageable) {

        // Gọi repository với từ khóa tìm kiếm
        Page<Post> postPage = postRepository.findPostsByKeyword(keyword, pageable);

        // Nếu không có bài viết nào tìm thấy, ném ngoại lệ
        if (!postPage.hasContent()) {
            throw new EntityNotFoundException("Không tìm thấy bài viết nào với từ khóa: " + keyword);
        }

        // Chuyển đổi sang PostResponseDTO
        return postPage.map(postMapper::toPostResponseDTO);
    }

    @Override
    public Page<PostAdminDTO> getPostsForAdmin(PostStatus status, Pageable pageable) {
        // Lấy danh sách bài viết theo trạng thái và phân trang từ repository
        Page<Post> postPage = postRepository.findByStatusOrderByCreatedAtDesc(status, pageable);

        // Tạo danh sách PostAdminDTO từ danh sách Post
        Page<PostAdminDTO> postAdminDTOs = postPage.map(this::convertToPostAdminDTO);

        return postAdminDTOs;
    }

    // Phương thức riêng để chuyển đổi Post thành PostAdminDTO
    private PostAdminDTO convertToPostAdminDTO(Post post) {
        // Sử dụng MapStruct để chuyển đổi Post sang PostAdminDTO
        return postMapper.toPostAdminDTO(post);
    }
    @Override
    public Page<PostAdminDTO> getAllReportedPosts(Pageable pageable) {
        // Lấy danh sách bài viết bị tố cáo từ repository
        Page<Post> postPage = reportPostRepository.findAllReportedPostsOrderByCreatedAtDesc(pageable);

        // Ánh xạ từ Post sang PostAdminDTO
        return postPage.map(this::convertToPostAdminDTO);
    }

    @Override
    public PostDetailResponseDTO getPostAdminById(int postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);

        // Nếu tìm thấy Post, chuyển đổi sang PostDetailResponseDTO
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            return postMapper.toPostDetailResponseDTO(post);
        } else {
            throw new EntityNotFoundException("Post not found with ID: " + postId);
        }
    }

    private Post findPostByUuidOrThrow(String postUuid) {
        return postRepository.findByPostUuid(postUuid)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng với UUID: " + postUuid));
    }

    @Override
    public List<SimilarPostResponse> getSimilarPosts(String postUuid) {
        Post post = findPostByUuidOrThrow(postUuid); // Gọi hàm riêng
        List<Post> similarPosts = postRepository.findByRoom_CityAndRoom_DistrictAndRoom_WardOrderByCreatedAtDesc(
                post.getRoom().getCity(),
                post.getRoom().getDistrict(),
                post.getRoom().getWard()
        );
        return similarPosts.stream()
                .filter(p -> !p.getPostUuid().equals(postUuid))
                .map(postMapper::toSimilarPostResponse) // Sử dụng MapStruct
                .collect(Collectors.toList());
    }
//
//    @Override
//    public PostStatsResponse getPostStatsByYear(int year) {
//        List<Post> approvedPosts = postRepository.findByYearAndStatus(year, PostStatus.APPROVED);
//        List<Post> rejectedPosts = postRepository.findByYearAndStatus(year, PostStatus.REJECTED);
//
//        List<Integer> approvedCounts = new ArrayList<>(List.of(new Integer[12]));
//        List<Integer> rejectedCounts = new ArrayList<>(List.of(new Integer[12]));
//        for (Post post : approvedPosts) {
//            int month = post.getCreatedAt().getMonthValue() - 1; // Month value is 1-based
//            approvedCounts.set(month, approvedCounts.get(month) == null ? 1 : approvedCounts.get(month) + 1);
//        }
//
//        for (Post post : rejectedPosts) {
//            int month = post.getCreatedAt().getMonthValue() - 1; // Month value is 1-based
//            rejectedCounts.set(month, rejectedCounts.get(month) == null ? 1 : rejectedCounts.get(month) + 1);
//        }
//
//        // Initialize null values to 0 for proper JSON serialization
//        for (int i = 0; i < 12; i++) {
//            if (approvedCounts.get(i) == null) {
//                approvedCounts.set(i, 0);
//            }
//            if (rejectedCounts.get(i) == null) {
//                rejectedCounts.set(i, 0);
//            }
//        }
//
//        PostStatsResponse response = new PostStatsResponse();
//        response.setApprovedCounts(approvedCounts);
//        response.setRejectedCounts(rejectedCounts);
//
//        return response;
//    }

    @Override
    public PostStatsResponse getPostStatsByYear(int year) {
        log.info("Fetching approved posts for year: {}", year);
        List<Post> approvedPosts = postRepository.findByYearAndStatus(year, PostStatus.APPROVED);
        log.info("Found {} approved posts.", approvedPosts.size());

        log.info("Fetching rejected posts for year: {}", year);
        List<Post> rejectedPosts = postRepository.findByYearAndStatus(year, PostStatus.REJECTED);
        log.info("Found {} rejected posts.", rejectedPosts.size());

        List<Integer> approvedCounts = new ArrayList<>(Collections.nCopies(12, 0));
        List<Integer> rejectedCounts = new ArrayList<>(Collections.nCopies(12, 0));
        log.info("Hi");
        for (Post post : approvedPosts) {
            log.info("Processing approved post created at: {}", post.getCreatedAt());
            if (post.getCreatedAt() != null) {
                int month = post.getCreatedAt().getMonthValue() - 1;
                approvedCounts.set(month, approvedCounts.get(month) == null ? 1 : approvedCounts.get(month) + 1);
            } else {
                log.warn("Approved post has null createdAt!");
            }
        }

        for (Post post : rejectedPosts) {
            log.info("Processing rejected post created at: {}", post.getCreatedAt());
            if (post.getCreatedAt() != null) {
                int month = post.getCreatedAt().getMonthValue() - 1;
                rejectedCounts.set(month, rejectedCounts.get(month) == null ? 1 : rejectedCounts.get(month) + 1);
            } else {
                log.warn("Rejected post has null createdAt!");
            }
        }

        for (int i = 0; i < 12; i++) {
            if (approvedCounts.get(i) == null) {
                approvedCounts.set(i, 0);
            }
            if (rejectedCounts.get(i) == null) {
                rejectedCounts.set(i, 0);
            }
        }

        PostStatsResponse response = new PostStatsResponse();
        response.setApprovedCounts(approvedCounts);
        response.setRejectedCounts(rejectedCounts);

        log.info("Returning PostStatsResponse: {}", response);
        return response;
    }

    @Override
    @Transactional
    public PostDetailResponseDTO makePostAnonymous(String postUuid, Integer currentUserId) {
        Post post = findPostByUuidOrThrow(postUuid);

        // Kiểm tra quyền sở hữu bài viết
        if (!post.getUser().getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("Bạn không có quyền thực hiện thao tác này.");
        }

        // Kiểm tra trạng thái hiện tại của bài viết
        if (post.getStatus() != PostStatus.APPROVED) {
            throw new IllegalArgumentException("Chỉ có thể ẩn danh bài viết đã được duyệt.");
        }

        // Cập nhật trạng thái thành ẩn danh
        post.setStatus(PostStatus.ANONYMOUS);
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        return postMapper.toPostDetailResponseDTO(savedPost);
    }

    @Override
    @Transactional
    public PostDetailResponseDTO makePostUnanonymous(String postUuid, Integer currentUserId) {
        Post post = findPostByUuidOrThrow(postUuid);

        // Kiểm tra quyền sở hữu bài viết
        if (!post.getUser().getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("Bạn không có quyền thực hiện thao tác này.");
        }

        // Kiểm tra trạng thái hiện tại của bài viết
        if (post.getStatus() != PostStatus.ANONYMOUS) {
            throw new IllegalArgumentException("Bài viết không ở trạng thái ẩn danh.");
        }

        // Cập nhật trạng thái thành đã duyệt
        post.setStatus(PostStatus.APPROVED);
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        return postMapper.toPostDetailResponseDTO(savedPost);
    }
}
