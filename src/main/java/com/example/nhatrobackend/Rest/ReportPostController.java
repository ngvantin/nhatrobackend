package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ReportPostRequestDTO;
import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.Entity.ReportPost;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.ReportPostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

