package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.NotificationEvent;
import com.example.nhatrobackend.DTO.ReportPostAdminDTO;
import com.example.nhatrobackend.DTO.ReportPostDetailDTO;
import com.example.nhatrobackend.DTO.ReportPostRequestDTO;
import com.example.nhatrobackend.DTO.response.NotificationResponse;
import com.example.nhatrobackend.Entity.Field.EventType;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Entity.Field.ReportStatus;
import com.example.nhatrobackend.Entity.Field.Status;
import com.example.nhatrobackend.Entity.Notification;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.ReportImage;
import com.example.nhatrobackend.Entity.ReportPost;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.PostMapper;
import com.example.nhatrobackend.Mapper.ReportPostMapper;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Responsitory.ReportImageRepository;
import com.example.nhatrobackend.Responsitory.ReportPostRepository;
import com.example.nhatrobackend.Service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportPostServiceImpl implements ReportPostService {
    private final ReportPostRepository reportPostRepository;
    private final UserService userService;
    private final PostService postService;
    private final ReportPostMapper reportPostMapper;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final MailService mailService;
    private final UploadImageFileService uploadImageFileService;
    private final ReportImageRepository reportImageRepository;
    @Value("${spring.application.serverName}")
    private String serverName;

//    @Override
//    public Page<ReportPost> getAllReportedPosts(Pageable pageable) {
//        return reportPostRepository.findAllReportedPosts(pageable);
//    }

    @Override
    @Transactional
    public ReportPost createReportPost(ReportPostRequestDTO requestDTO, String postUuid, String userUuid) {
        log.info("Creating report for post {} by user {}", postUuid, userUuid);

        // Lấy User và Post từ các service tương ứng
        User user = userService.getUserByUuid(userUuid);
        Post post = postService.getPostByUuid(postUuid);

        // Tạo mới ReportPost
        ReportPost reportPost = new ReportPost();
        reportPost.setReason(requestDTO.getReason());
        reportPost.setDetails(requestDTO.getDetails());
        reportPost.setCreatedAt(LocalDateTime.now());
        reportPost.setUpdatedAt(LocalDateTime.now());
        reportPost.setUser(user);
        reportPost.setPost(post);
        reportPost.setStatus(ReportStatus.PENDING);

        // Upload video if provided
        if (requestDTO.getVideo() != null && !requestDTO.getVideo().isEmpty()) {
            try {
                String videoUrl = uploadImageFileService.uploadImage(requestDTO.getVideo());
                reportPost.setVideoUrl(videoUrl);
                log.info("Uploaded video for report: {}", videoUrl);
            } catch (IOException e) {
                log.error("Failed to upload video for report", e);
                // Handle exception appropriately, maybe re-throw or return an error response
            }
        }

        // Save ReportPost first to get its ID
        ReportPost savedReportPost = reportPostRepository.save(reportPost);
        log.info("Saved report post with ID: {}", savedReportPost.getReportId());

        // Upload images if provided
        List<ReportImage> reportImages = new ArrayList<>();
        if (requestDTO.getImages() != null && !requestDTO.getImages().isEmpty()) {
            log.info("Uploading {} images for report", requestDTO.getImages().size());
            for (MultipartFile imageFile : requestDTO.getImages()) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    try {
                        String imageUrl = uploadImageFileService.uploadImage(imageFile);
                        ReportImage reportImage = new ReportImage(imageUrl, savedReportPost);
                        reportImages.add(reportImage);
                        log.info("Uploaded image for report: {}", imageUrl);
                    } catch (IOException e) {
                        log.error("Failed to upload image for report", e);
                        // Handle exception appropriately
                    }
                }
            }
             // Save all report images in a batch (optional but can be more efficient)
             reportImageRepository.saveAll(reportImages);
             log.info("Saved {} report images", reportImages.size());
        }

        // The savedReportPost object should now have its ID and the reportImages list should be populated after saving the images
        // If you need the reportImages list on the returned object immediately, you might need to refresh it or fetch it again.
        // However, since it's a Transactional method and the entities are managed, they should be associated.

        return savedReportPost;
    }

    @Override
    public Page<ReportPostAdminDTO> getAllReportedPosts(Pageable pageable) {
        // Lấy tất cả bài viết bị tố cáo có trạng thái PENDING từ repository, phân trang
        Page<ReportPost> reportPosts = reportPostRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.PENDING, pageable);

        // Sử dụng MapStruct để chuyển đổi từ ReportPost sang ReportPostDTO
        return reportPosts.map(reportPostMapper::reportPostToReportPostAdminDTO);
    }

    @Override
    public ReportPostDetailDTO getReportPostDetail(Integer reportId) {
        // Lấy ReportPost theo reportId
        Optional<ReportPost> optionalReportPost = reportPostRepository.findByReportId(reportId);

        if (!optionalReportPost.isPresent()) {
            throw new EntityNotFoundException("ReportPost not found with ID: " + reportId);
        }

        ReportPost reportPost = optionalReportPost.get();

        // Map ReportPost entity to ReportPostDetailDTO
        ReportPostDetailDTO reportPostDetailDTO = ReportPostDetailDTO.builder()
                .reportId(reportPost.getReportId())
                .reason(reportPost.getReason())
                .details(reportPost.getDetails())
                .createdAt(reportPost.getCreatedAt())
                .videoUrl(reportPost.getVideoUrl()) // Include video URL
                .reportImages(reportPost.getReportImages() != null ? 
                              reportPost.getReportImages().stream()
                                  .map(ReportImage::getImageUrl)
                                  .collect(Collectors.toList()) : new ArrayList<>()) // Include image URLs
                .build();

        // Chuyển đổi và trả về DTO
        return reportPostDetailDTO;
    }

    @Override
    @Transactional
    public void approveReportPost(Integer reportId) {
        // Tìm báo cáo bằng reportId
        ReportPost reportPost = reportPostRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Báo cáo không tồn tại với ID: " + reportId));

        // Kiểm tra trạng thái hiện tại của báo cáo
        if (reportPost.getStatus() != ReportStatus.PENDING) {
            throw new IllegalStateException("Báo cáo này đã được xử lý trước đó.");
        }

        // Lấy bài viết liên quan
        Post post = reportPost.getPost();
        if (post == null) {
            throw new EntityNotFoundException("Không tìm thấy bài viết liên quan đến báo cáo này.");
        }

        // Cập nhật trạng thái bài viết thành LOCKED
        post.setStatus(PostStatus.LOCKED);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        // Cập nhật trạng thái báo cáo thành APPROVED
        reportPost.setStatus(ReportStatus.APPROVED);
        reportPost.setUpdatedAt(LocalDateTime.now());
        reportPostRepository.save(reportPost);


        // Tạo và lưu notification vào database
        Notification notification = Notification.builder()
                .title("Bài đăng đã bị khóa")
                .content("Bài đăng " + post.getTitle() + " của bạn đã bị khóa vì " + reportPost.getReason())
                .type(EventType.POST_LOCKED.name())
                .userId(post.getUser().getUserId())
                .postId(post.getPostId())
                .redirectUrl("/posts/" + post.getPostId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Lưu notification vào database
        Notification savedNotification = notificationService.save(notification);
        // Tạo và gửi notification event
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("postId", post.getPostId());  // Use null directly since it's allowed in HashMap
        metadata.put("userId", post.getUser().getUserId());  // Use Integer directly

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
                .type(EventType.POST_LOCKED)
                .notification(notificationResponse)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .priority(NotificationEvent.Priority.HIGH)
                .status(Status.PENDING)
                .build();

        // Gửi notification
        notificationService.sendNotification(event);

//        // Gửi email thông báo
//        try {
//            String postUrl = String.format("%s/posts/%d", serverName, user.getUserId());
//            mailService.send(user.getEmail());
//        } catch (Exception e) {
//            log.error("Failed to send post rejection email", e);
//            // Không throw exception vì đây không phải là lỗi nghiêm trọng
//        }
    }

    @Override
    @Transactional
    public void rejectReportPost(Integer reportId) {
        // Tìm báo cáo bằng reportId
        ReportPost reportPost = reportPostRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Báo cáo không tồn tại với ID: " + reportId));

        // Kiểm tra trạng thái hiện tại của báo cáo
        if (reportPost.getStatus() != ReportStatus.PENDING) {
            throw new IllegalStateException("Báo cáo này đã được xử lý trước đó.");
        }


        // Cập nhật trạng thái báo cáo thành APPROVED
        reportPost.setStatus(ReportStatus.REJECTED);
        reportPost.setUpdatedAt(LocalDateTime.now());
        reportPostRepository.save(reportPost);
    }


}
