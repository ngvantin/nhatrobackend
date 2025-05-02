package com.example.nhatrobackend.Rest;


import com.example.nhatrobackend.DTO.PostDetailResponseDTO;
import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.request.PaymentRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
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
    @PostMapping("/vn-pay")
    public ResponseEntity<ResponseWrapper<VNPayResponse>> pay(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        return ResponseEntity.ok(ResponseWrapper.<VNPayResponse>builder()
                .status("success")
                .data(paymentService.createVnPayPayment(paymentRequest, request))
                .message("Thanh toán VNPAY")
                .build());
    }
//
//    @GetMapping("/vn-pay-callback")
//    public ResponseObject<PaymentDTO.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
//        String status = request.getParameter("vnp_ResponseCode");
//        if (status.equals("00")) {
//            return new ResponseObject<>(HttpStatus.OK, "Success", new PaymentDTO.VNPayResponse("00", "Success", ""));
//        } else {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Failed", null);
//        }
//    }
}
