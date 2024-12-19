package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.ReportPostAdminDTO;
import com.example.nhatrobackend.DTO.ReportPostDetailDTO;
import com.example.nhatrobackend.DTO.ReportPostRequestDTO;
import com.example.nhatrobackend.Entity.Field.PostStatus;
import com.example.nhatrobackend.Entity.Field.ReportStatus;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.ReportPost;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.PostMapper;
import com.example.nhatrobackend.Mapper.ReportPostMapper;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Responsitory.ReportPostRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportPostServiceImpl implements ReportPostService {
    private final ReportPostRepository reportPostRepository;
    private final UserService userService;
    private final PostService postService;
    private final ReportPostMapper reportPostMapper;
    private final PostMapper postMapper;
    private final PostRepository postRepository;

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

    @Override
    public ReportPostDetailDTO getReportPostDetail(Integer reportId) {
        // Lấy ReportPost theo reportId
        Optional<ReportPost> optionalReportPost = reportPostRepository.findById(reportId);

        if (!optionalReportPost.isPresent()) {
            throw new EntityNotFoundException("ReportPost not found with ID: " + reportId);
        }

        ReportPost reportPost = optionalReportPost.get();
        Post post = reportPost.getPost(); // Lấy Post liên quan

        // Chuyển đổi và trả về DTO
        return postMapper.toReportPostDetailDTO(post, reportPost);
    }

    // Phương thức duyệt bài viết bị tố cáo
    @Transactional
    public void approveReportPost(Integer reportId, String reason) {
        // Tìm báo cáo bằng reportId
        Optional<ReportPost> optionalReportPost = reportPostRepository.findById(reportId);
        if (!optionalReportPost.isPresent()) {
            throw new EntityNotFoundException("Báo cáo không tồn tại với ID: " + reportId);
        }

        ReportPost reportPost = optionalReportPost.get();

        // Tìm bài viết liên quan từ báo cáo
        Post post = reportPost.getPost();

        // Cập nhật trạng thái bài viết thành LOCKED
        post.setStatus(PostStatus.LOCKED);
        postRepository.save(post);  // Lưu lại bài viết

        // Cập nhật trạng thái của báo cáo thành APPROVED
        reportPost.setStatus(ReportStatus.APPROVED);
        reportPost.setAdminResponse(reason);  // Lý do duyệt
        reportPostRepository.save(reportPost);  // Lưu lại báo cáo
    }

    @Transactional
    public void rejectReportPost(Integer reportId, String reason) {
        // Tìm báo cáo bằng reportId
        Optional<ReportPost> optionalReportPost = reportPostRepository.findById(reportId);
        if (!optionalReportPost.isPresent()) {
            throw new EntityNotFoundException("Báo cáo không tồn tại với ID: " + reportId);
        }

        ReportPost reportPost = optionalReportPost.get();

        // Cập nhật trạng thái của báo cáo thành REJECTED
        reportPost.setStatus(ReportStatus.REJECTED);
        reportPost.setAdminResponse(reason);  // Lý do từ chối
        reportPostRepository.save(reportPost);  // Lưu lại báo cáo
    }


}
