package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.request.DepositRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.DepositService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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

    @GetMapping("/{depositId}")
    public ResponseEntity<ResponseWrapper<?>> getDepositDetails(@PathVariable Integer depositId) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status("success")
                .data(depositService.getDepositDetails(depositId, currentUserId))
                .message("Chi tiết đơn đặt cọc")
                .build());
    }

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
} 