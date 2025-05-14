package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.response.PaymentHistoryResponse;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
