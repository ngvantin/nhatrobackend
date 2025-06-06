package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.response.PaymentHistoryResponse;
import com.example.nhatrobackend.DTO.response.MonthlyRevenueResponse;
import com.example.nhatrobackend.DTO.response.AdminPaymentHistoryResponse;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payment-histories")
@RequiredArgsConstructor
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;
    private final AuthenticationFacade authenticationFacade;

    @GetMapping("/user")
    public ResponseEntity<ResponseWrapper<List<PaymentHistoryResponse>>> getPaymentHistoriesForCurrentUser() {
        Integer userId = authenticationFacade.getCurrentUserId();
        List<PaymentHistoryResponse> paymentHistories = paymentHistoryService.getPaymentHistoriesByUserId(userId);

        return ResponseEntity.ok(ResponseWrapper.<List<PaymentHistoryResponse>>builder()
                .status("success")
                .data(paymentHistories)
                .message("Lịch sử thanh toán của người dùng")
                .build());
    }

    @GetMapping("/user/{paymentId}")
    public ResponseEntity<ResponseWrapper<PaymentHistoryResponse>> getPaymentDetailsById(
            @PathVariable Long paymentId) {
        Integer userId = authenticationFacade.getCurrentUserId();
        PaymentHistoryResponse paymentDetails = paymentHistoryService.getPaymentDetailsById(userId, paymentId);

        return ResponseEntity.ok(ResponseWrapper.<PaymentHistoryResponse>builder()
                .status("success")
                .data(paymentDetails)
                .message("Chi tiết giao dịch")
                .build());
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<ResponseWrapper<List<Long>>> getMonthlyRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Long> monthlyRevenues = paymentHistoryService.getMonthlyRevenue(startDate, endDate);
        
        return ResponseEntity.ok(ResponseWrapper.<List<Long>>builder()
                .status("success")
                .data(monthlyRevenues)
                .message("Doanh thu theo tháng")
                .build());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ResponseWrapper<Page<PaymentHistoryResponse>>> getAllPaymentHistories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentTime") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Page<PaymentHistoryResponse> paymentHistories = paymentHistoryService.getAllPaymentHistories(
                page, size, sortBy, direction);
        
        return ResponseEntity.ok(ResponseWrapper.<Page<PaymentHistoryResponse>>builder()
                .status("success")
                .data(paymentHistories)
                .message("Danh sách tất cả giao dịch")
                .build());
    }

    @GetMapping("/admin/{paymentId}")
    public ResponseEntity<ResponseWrapper<AdminPaymentHistoryResponse>> getAdminPaymentDetails(
            @PathVariable Long paymentId) {
        AdminPaymentHistoryResponse paymentDetails = paymentHistoryService.getAdminPaymentDetailsById(paymentId);
        
        return ResponseEntity.ok(ResponseWrapper.<AdminPaymentHistoryResponse>builder()
                .status("success")
                .data(paymentDetails)
                .message("Chi tiết giao dịch")
                .build());
    }
}
