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
    public ResponseEntity<ResponseWrapper<String>> payCallbackHandler(HttpServletRequest request) {
        String result = paymentService.processVnPayCallback(request);
        String[] parts = result.split("\\|");
        String status = parts[0];
        String message = parts.length > 1 ? parts[1] : "";

        return switch (status) {
            case "success" -> ResponseEntity.ok(ResponseWrapper.<String>builder()
                    .status("success")
                    .message("Thanh toán thành công. " + message)
                    .build());
            case "fail" -> ResponseEntity.ok(ResponseWrapper.<String>builder()
                    .status("fail")
                    .message("Thanh toán thất bại. " + message)
                    .build());
            case "error" -> ResponseEntity.badRequest().body(ResponseWrapper.<String>builder()
                    .status("error")
                    .message(message)
                    .build());
            default -> ResponseEntity.internalServerError().body(ResponseWrapper.<String>builder()
                    .status("error")
                    .message("Lỗi không xác định trong quá trình xử lý callback.")
                    .build());
        };
    }
}
