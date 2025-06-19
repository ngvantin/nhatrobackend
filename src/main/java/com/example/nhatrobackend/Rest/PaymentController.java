package com.example.nhatrobackend.Rest;


import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.request.PaymentRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final AuthenticationFacade authenticationFacade;
    @PostMapping("/vn-pay")
    public ResponseEntity<ResponseWrapper<VNPayResponse>> pay(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        Integer currentUserId = authenticationFacade.getCurrentUserId();

        return ResponseEntity.ok(ResponseWrapper.<VNPayResponse>builder()
                .status("success")
                .data(paymentService.createVnPayPayment(paymentRequest, request,currentUserId))
                .message("Thanh toán VNPAY")
                .build());
    }



    @GetMapping("/vn-pay-callback")
    public ResponseEntity<Void> payCallbackHandler(HttpServletRequest request) {
        // Xử lý callback từ VNPay
        String result = paymentService.processVnPayCallback(request);
        String[] parts = result.split("\\|");
        String status = parts[0];
        String message = parts.length > 1 ? parts[1] : "";

        // Tạo URL chuyển hướng tới frontend với các tham số status và message
        String redirectUrl = UriComponentsBuilder.fromHttpUrl("https://fe-timkiemtro.vercel.app/users/payment-result")
                .queryParam("status", status)
                .queryParam("message", message)
                .toUriString();

        // Chuyển hướng người dùng tới frontend
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
    }
}
