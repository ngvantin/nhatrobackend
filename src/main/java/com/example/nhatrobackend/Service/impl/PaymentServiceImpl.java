package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.Config.VNPAYConfig;
import com.example.nhatrobackend.DTO.request.PaymentRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
import com.example.nhatrobackend.Entity.PaymentHistory;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Responsitory.PaymentHistoryRepository;
import com.example.nhatrobackend.Service.PaymentService;
import com.example.nhatrobackend.Service.UserService;
import com.example.nhatrobackend.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final VNPAYConfig vnPayConfig;
    private final UserService userService;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final static Map<Long, Integer> PAYMENT_PACKAGES = Map.of(
            100000L, 5,
            300000L, 20,
            700000L, 50
    );
    @Override
    public VNPayResponse createVnPayPayment(PaymentRequest paymentRequest, HttpServletRequest request,Integer currentUserId) {
        long amount = paymentRequest.getAmount() * 100L;
        String bankCode = paymentRequest.getBankCode();
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_OrderInfo", "Mua " + PAYMENT_PACKAGES.getOrDefault(paymentRequest.getAmount().longValue(), 0) + " luot dang bai - UserID: " + currentUserId);

        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }

    @Override
    public String processVnPayCallback(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        String transactionCode = request.getParameter("vnp_TxnRef");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String amountStr = request.getParameter("vnp_Amount");
        String vnpSecureHash = request.getParameter("vnp_SecureHash");
        String vnpHashSecret = vnPayConfig.getSecretKey();

        Map<String, String> vnpParams = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {
            if (key.startsWith("vnp_")) {
                vnpParams.put(key, value[0]);
            }
        });

        String calculatedHash = VNPayUtil.hashAllFields(vnpParams, vnpHashSecret);

        if (calculatedHash.equals(vnpSecureHash)) {
            if ("00".equals(responseCode)) {
                Long amount = Long.parseLong(amountStr) / 100;
                return "success|" + processPaymentSuccess(transactionCode, amount, orderInfo); // Trả về trạng thái và thông báo
            } else {
                return "fail|" + processPaymentFailed(transactionCode, responseCode, orderInfo); // Trả về trạng thái và thông báo lỗi
            }
        } else {
            return "error|Lỗi chữ ký không hợp lệ"; // Trả về trạng thái lỗi
        }
    }

    @Override
    @Transactional
    public String processPaymentSuccess(String transactionCode, Long amount, String orderInfo) {
        String[] parts = orderInfo.split(" - UserID: ");
        Integer userId = null;
        if (parts.length == 2) {
            try {
                userId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return "Không tìm thấy thông tin UserID trong orderInfo.";
            }
        } else {
            return "Định dạng orderInfo không hợp lệ.";
        }

        User user = userService.findByUserId(userId);

        Integer purchasedPosts = PAYMENT_PACKAGES.getOrDefault(amount, 0);
        user.setPostCount(user.getPostCount() + purchasedPosts);
        userService.save(user); // Sử dụng UserService để lưu user

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .user(user)
                .paymentAmount(amount * 100)
                .transactionCode(transactionCode)
                .responseCode("00")
                .orderInfo(orderInfo)
                .paymentTime(LocalDateTime.now())
                .build();
        paymentHistoryRepository.save(paymentHistory);

        return "Đã cộng " + purchasedPosts + " lượt đăng bài vào tài khoản.";
    }

    @Override
    public String processPaymentFailed(String transactionCode, String responseCode, String orderInfo) {
        Integer userId = null;
        String[] parts = orderInfo.split(" - UserID: ");
        if (parts.length == 2) {
            try {
                userId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                // Xử lý nếu không tìm thấy UserID hợp lệ
                System.err.println("Không tìm thấy UserID hợp lệ trong orderInfo cho giao dịch thất bại.");
            }
        } else {
            System.err.println("Định dạng orderInfo không hợp lệ cho giao dịch thất bại.");
        }

        User user = null;
        if (userId != null) {
            user = userService.findByUserId(userId);
        }

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .user(user) // Set user (có thể là null nếu không tìm thấy)
                .paymentAmount(Long.parseLong(orderInfo.split(" ")[1].replace("luot", "").replace("bai", "")) * 100)
                .transactionCode(transactionCode)
                .responseCode(responseCode)
                .orderInfo(orderInfo)
                .paymentTime(LocalDateTime.now())
                .build();
        paymentHistoryRepository.save(paymentHistory);
        return "Giao dịch thanh toán thất bại.";
    }
}
