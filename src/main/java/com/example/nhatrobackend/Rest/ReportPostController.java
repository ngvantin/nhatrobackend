package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Entity.ReportPost;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.ReportPostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportPostController {

    private final ReportPostService reportPostService;

    private final AuthenticationFacade authenticationFacade;

    @PostMapping("/create/{postUuid}")
    public ResponseEntity<ResponseWrapper<String>> createReportPost(
            @PathVariable String postUuid,
            @RequestBody ReportPostRequestDTO requestDTO, HttpServletRequest request) {

        //        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
        String userUuid = authenticationFacade.getCurrentUserUuid(request);

        reportPostService.createReportPost(requestDTO, postUuid, userUuid);

        return  ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message("Báo cáo bài đăng thành công.")
                .build());
    }
    @GetMapping("/admin/all")
    public ResponseEntity<ResponseWrapper<Page<ReportPostAdminDTO>>> getAllReportedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Lấy danh sách bài viết bị tố cáo và trả về DTO
        Page<ReportPostAdminDTO> reportPostDTOPage = reportPostService.getAllReportedPosts(pageable);
        return ResponseEntity.ok(ResponseWrapper.<Page<ReportPostAdminDTO>>builder()
                .status("success")
                .data(reportPostDTOPage)
                .message("Thông tin bài đăng")
                .build());
    }

    @GetMapping("/admin/detail/{reportId}")
    public ResponseEntity<ResponseWrapper<ReportPostDetailDTO>> getReportPostDetail(@PathVariable Integer reportId) {
        ReportPostDetailDTO reportPostDetailDTO = reportPostService.getReportPostDetail(reportId);
        return ResponseEntity.ok(ResponseWrapper.<ReportPostDetailDTO>builder()
                .status("success")
                .data(reportPostDetailDTO)
                .message("Thông tin chi tiết báo cáo bài đăng")
                .build());
    }

    // API duyệt bài viết bị tố cáo
    @PutMapping("/admin/approve/{reportId}")
    public ResponseEntity<ResponseWrapper<String>> approveReportPost(@PathVariable Integer reportId,
                                                                     @RequestBody ApproveRequest request) {
        String reason = request.getReason();
        reportPostService.approveReportPost(reportId, reason);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data("Bài viết đã được duyệt.")
                .message("Duyệt bài viết thành công")
                .build());
    }

    // API từ chối bài viết bị tố cáo
    @PutMapping("/admin/reject/{reportId}")
    public ResponseEntity<ResponseWrapper<String>> rejectReportPost(@PathVariable Integer reportId,
                                                                    @RequestBody ApproveRequest request) {
        String reason = request.getReason();
        reportPostService.rejectReportPost(reportId, reason);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data("Bài viết đã bị từ chối.")
                .message("Từ chối bài viết thành công")
                .build());
    }

}

