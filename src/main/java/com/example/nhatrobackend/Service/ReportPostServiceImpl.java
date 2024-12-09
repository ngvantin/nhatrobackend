package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.ReportPostRequestDTO;
import com.example.nhatrobackend.Entity.Field.ReportStatus;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.ReportPost;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Responsitory.ReportPostRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportPostServiceImpl implements ReportPostService {
    private final ReportPostRepository reportPostRepository;
    private final UserService userService;
    private final PostService postService;

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
}
