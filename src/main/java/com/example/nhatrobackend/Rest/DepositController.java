package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.request.DepositRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
import com.example.nhatrobackend.Entity.Field.DepositStatus;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.DepositService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/deposit")
@RequiredArgsConstructor
public class DepositController {
    private final DepositService depositService;
    private final AuthenticationFacade authenticationFacade;

    @PostMapping("/vn-pay")
    public ResponseEntity<ResponseWrapper<VNPayResponse>> createDeposit(
            @RequestBody DepositRequest depositRequest,
            HttpServletRequest request) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();

        return ResponseEntity.ok(ResponseWrapper.<VNPayResponse>builder()
                .status("success")
                .data(depositService.createDepositPayment(depositRequest, request, currentUserId))
                .message("Tạo đơn đặt cọc")
                .build());
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<Void> depositCallbackHandler(HttpServletRequest request) {
        String result = depositService.processDepositCallback(request);
        String[] parts = result.split("\\|");
        String status = parts[0];
        String message = parts.length > 1 ? parts[1] : "";

        String redirectUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:5173/users/deposit-result")
                .queryParam("status", status)
                .queryParam("message", message)
                .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
    }

//    @GetMapping("/{depositId}")
//    public ResponseEntity<ResponseWrapper<?>> getDepositDetails(@PathVariable Integer depositId) {
//        Integer currentUserId = authenticationFacade.getCurrentUserId();
//        return ResponseEntity.ok(ResponseWrapper.builder()
//                .status("success")
//                .data(depositService.getDepositDetails(depositId, currentUserId))
//                .message("Chi tiết đơn đặt cọc")
//                .build());
//    }

    @PostMapping("/{depositId}/confirm")
    public ResponseEntity<ResponseWrapper<?>> confirmDeposit(
            @PathVariable Integer depositId,
            @RequestParam Boolean isConfirmed) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status("success")
                .data(depositService.confirmDeposit(depositId, currentUserId, isConfirmed))
                .message("Xác nhận đơn đặt cọc")
                .build());
    }

    @GetMapping("/posts")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> getDepositedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(ResponseWrapper.<Page<PostResponseDTO>>builder()
                .status("success")
                .data(depositService.getDepositedPosts(currentUserId, pageable))
                .message("Lấy danh sách bài đăng đã đặt cọc thành công")
                .build());
    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<ResponseWrapper<Page<DepositResponseDTO>>> getDepositsByUser(
//            @PathVariable Integer userId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<DepositResponseDTO> deposits = depositService.getDepositsByUser(userId, pageable);
//        return ResponseEntity.ok(ResponseWrapper.<Page<DepositResponseDTO>>builder()
//                .status("success")
//                .data(deposits)
//                .message("Danh sách đặt cọc của người dùng.")
//                .build());
//    }

    @GetMapping("/posts-with-deposits")
    public ResponseEntity<ResponseWrapper<Page<PostResponseDTO>>> getPostsWithDepositsByOtherUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponseDTO> posts = depositService.getPostsWithDepositsByOtherUsers(currentUserId, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Page<PostResponseDTO>>builder()
                .status("success")
                .data(posts)
                .message("Danh sách bài đăng có người khác đặt cọc.")
                .build());
    }

    @GetMapping("/post/{postId}/users")
    public ResponseEntity<ResponseWrapper<List<UserDepositDTO>>> getUsersWithDepositsByPostId(
            @PathVariable Integer postId) {
        List<UserDepositDTO> users = depositService.getUsersWithDepositsByPostId(postId);
        return ResponseEntity.ok(ResponseWrapper.<List<UserDepositDTO>>builder()
                .status("success")
                .data(users)
                .message("Danh sách người dùng đã đặt cọc cho bài đăng.")
                .build());
    }

    @GetMapping("/{depositId}/details")
    public ResponseEntity<ResponseWrapper<DepositDetailDTO>> getDepositDetails(
            @PathVariable Integer depositId) {
        DepositDetailDTO depositDetails = depositService.getDepositDetailsById(depositId);
        return ResponseEntity.ok(ResponseWrapper.<DepositDetailDTO>builder()
                .status("success")
                .data(depositDetails)
                .message("Thông tin chi tiết đặt cọc.")
                .build());
    }

    @PostMapping("/{depositId}/confirm/tenant")
    public ResponseEntity<ResponseWrapper<String>> confirmByTenant(
            @PathVariable Integer depositId) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        String message = depositService.confirmByTenant(depositId, currentUserId);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(message)
                .message(message)
                .build());
    }

    @PostMapping("/{depositId}/confirm/landlord")
    public ResponseEntity<ResponseWrapper<String>> confirmByLandlord(
            @PathVariable Integer depositId) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        String message = depositService.confirmByLandlord(depositId, currentUserId);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(message)
                .message(message)
                .build());
    }

    @PostMapping(value = "/{depositId}/complaint/tenant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<String>> complaintByTenant(
            @PathVariable Integer depositId,
            @ModelAttribute DepositComplaintRequestDTO requestDTO) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        String message = depositService.complaintByTenant(depositId, currentUserId, requestDTO);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(message)
                .message(message)
                .build());
    }

    @PostMapping(value = "/{depositId}/complaint/landlord", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseWrapper<String>> complaintByLandlord(
            @PathVariable Integer depositId,
            @ModelAttribute DepositComplaintRequestDTO requestDTO) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        String message = depositService.complaintByLandlord(depositId, currentUserId, requestDTO);
        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .data(message)
                .message(message)
                .build());
    }

    @GetMapping("/{depositId}/full-details")
    public ResponseEntity<ResponseWrapper<DepositFullDetailDTO>> getFullDepositDetails(
            @PathVariable Integer depositId) {
        DepositFullDetailDTO depositDetails = depositService.getFullDepositDetails(depositId);
        return ResponseEntity.ok(ResponseWrapper.<DepositFullDetailDTO>builder()
                .status("success")
                .data(depositDetails)
                .message("Thông tin chi tiết đầy đủ của đặt cọc.")
                .build());
    }

    @GetMapping("/status/paid")
    public ResponseEntity<ResponseWrapper<Page<DepositStatusDTO>>> getDepositsByStatusPaid(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DepositStatusDTO> deposits = depositService.getDepositsByStatus(DepositStatus.PAID, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Page<DepositStatusDTO>>builder()
                .status("success")
                .data(deposits)
                .message("Danh sách đặt cọc với trạng thái " + DepositStatus.PAID)
                .build());
    }


    @GetMapping("/status/confirmed")
    public ResponseEntity<ResponseWrapper<Page<DepositStatusDTO>>> getDepositsByStatusConfirmed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DepositStatusDTO> deposits = depositService.getDepositsByStatus(DepositStatus.CONFIRMED, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Page<DepositStatusDTO>>builder()
                .status("success")
                .data(deposits)
                .message("Danh sách đặt cọc với trạng thái " + DepositStatus.CONFIRMED)
                .build());
    }


    @GetMapping("/status/canelled")
    public ResponseEntity<ResponseWrapper<Page<DepositStatusDTO>>> getDepositsByStatusCanelled(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DepositStatusDTO> deposits = depositService.getDepositsByStatus(DepositStatus.CANCELLED, pageable);
        return ResponseEntity.ok(ResponseWrapper.<Page<DepositStatusDTO>>builder()
                .status("success")
                .data(deposits)
                .message("Danh sách đặt cọc với trạng thái " + DepositStatus.CANCELLED)
                .build());
    }
} 