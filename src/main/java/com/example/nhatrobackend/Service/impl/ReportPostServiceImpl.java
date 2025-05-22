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
import com.example.nhatrobackend.Entity.ReportPost;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.PostMapper;
import com.example.nhatrobackend.Mapper.ReportPostMapper;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Responsitory.ReportPostRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
import com.example.nhatrobackend.Service.NotificationService;
import com.example.nhatrobackend.Service.PostService;
import com.example.nhatrobackend.Service.ReportPostService;
import com.example.nhatrobackend.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @Transactional
    public ReportPost createReportPost(ReportPostRequestDTO requestDTO, String postUuid, String userUuid) {
        // Lấy User và Post từ các service tương ứng
        User user = userService.getUserByUuid(userUuid);
        Post post = postService.getPostByUuid(postUuid);

        // Tạo mới ReportPost
        ReportPost reportPost = new ReportPost();
        reportPost.setReason(requestDTO.getReason());
        reportPost.setDetails(requestDTO.getDetails());
        reportPost.setCreatedAt(LocalDateTime.now());
        reportPost.setUser(user);
        reportPost.setPost(post);
        reportPost.setStatus(ReportStatus.PENDING); // Mặc định là PENDING

        // Lưu vào database
        return reportPostRepository.save(reportPost);
    }

    @Override
    public Page<ReportPostAdminDTO> getAllReportedPosts(Pageable pageable) {

        // Lấy tất cả bài viết bị tố cáo từ repository, phân trang
        Page<ReportPost> reportPosts = reportPostRepository.findAll(pageable);

        // Sử dụng MapStruct để chuyển đổi từ ReportPost sang ReportPostDTO
        return reportPosts.map(reportPostMapper::reportPostToReportPostAdminDTO);
    }

    // dính lỗi khi sửa security xóa bảng account
    @Override
    public ReportPostDetailDTO getReportPostDetail(Integer reportId) {
        // Lấy ReportPost theo reportId
        Optional<ReportPost> optionalReportPost = reportPostRepository.findByReportId(reportId);

        if (!optionalReportPost.isPresent()) {
            throw new EntityNotFoundException("ReportPost not found with ID: " + reportId);
        }

        ReportPost reportPost = optionalReportPost.get();
        ReportPostDetailDTO reportPostDetailDTO = ReportPostDetailDTO.builder()
                .reportId(reportPost.getReportId())
                .reason(reportPost.getReason())
                .details(reportPost.getDetails())
                .createdAt(reportPost.getCreatedAt())
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
                .metadata(Map.of(
                        "postId", post.getPostId(),
                        "userId", post.getUser().getUserId()
                ))
                .priority(NotificationEvent.Priority.HIGH)
                .status(Status.PENDING)
                .build();

        // Gửi notification
        notificationService.sendNotification(event);

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
