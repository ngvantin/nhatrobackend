package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.Config.VNPAYConfig;
import com.example.nhatrobackend.DTO.request.DepositRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
import com.example.nhatrobackend.Entity.Deposit;
import com.example.nhatrobackend.Entity.Field.DepositStatus;
import com.example.nhatrobackend.Entity.Post;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Responsitory.DepositRepository;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Service.DepositService;
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
public class DepositServiceImpl implements DepositService {
    private final VNPAYConfig vnPayConfig;
    private final UserService userService;
    private final PostRepository postRepository;
    private final DepositRepository depositRepository;

    @Override
    public VNPayResponse createDepositPayment(DepositRequest depositRequest, HttpServletRequest request, Integer currentUserId) {
        // Kiểm tra và lấy thông tin bài đăng
        Post post = postRepository.findById(depositRequest.getPostId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

        // Kiểm tra người dùng
        User user = userService.findByUserId(currentUserId);

        // Tạo đơn đặt cọc
        Deposit deposit = Deposit.builder()
                .user(user)
                .post(post)
                .amount(depositRequest.getAmount())
                .status(DepositStatus.PENDING)
                .paymentMethod("VNPAY")
                .holdUntil(LocalDateTime.now().plusHours(48)) // Giữ chỗ 48 giờ
                .createdAt(LocalDateTime.now())
                .build();
        depositRepository.save(deposit);

        // Tạo URL thanh toán VNPay
        long amount = depositRequest.getAmount().longValue() * 100L;
        String bankCode = depositRequest.getBankCode();
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfigDeposit();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        vnpParamsMap.put("vnp_OrderInfo", "Dat coc phong tro - DepositID: " + deposit.getDepositId());

        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    public String processDepositCallback(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        String transactionCode = request.getParameter("vnp_TxnRef");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String amountStr = request.getParameter("vnp_Amount");
        String vnpSecureHash = request.getParameter("vnp_SecureHash");

        Map<String, String> vnpParams = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {
            if (key.startsWith("vnp_")) {
                vnpParams.put(key, value[0]);
            }
        });

        String calculatedHash = VNPayUtil.hashAllFields(vnpParams, vnPayConfig.getSecretKey());

        if (calculatedHash.equals(vnpSecureHash)) {
            if ("00".equals(responseCode)) {
                return "success|" + processDepositSuccess(transactionCode, amountStr, orderInfo);
            } else {
                return "fail|" + processDepositFailed(transactionCode, responseCode, orderInfo);
            }
        } else {
            return "error|Lỗi chữ ký không hợp lệ";
        }
    }

    @Override
    @Transactional
    public Object getDepositDetails(Integer depositId, Integer currentUserId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt cọc"));

        // Kiểm tra quyền truy cập
        if (!deposit.getUser().getUserId().equals(currentUserId) && 
            !deposit.getPost().getUser().getUserId().equals(currentUserId)) {
            throw new RuntimeException("Không có quyền truy cập đơn đặt cọc này");
        }

        return deposit;
    }

    @Override
    @Transactional
    public Object confirmDeposit(Integer depositId, Integer currentUserId, Boolean isConfirmed) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt cọc"));

        // Kiểm tra quyền xác nhận
        if (!deposit.getPost().getUser().getUserId().equals(currentUserId)) {
            throw new RuntimeException("Chỉ chủ trọ mới có quyền xác nhận đơn đặt cọc");
        }

        if (isConfirmed) {
            deposit.setLandlordConfirmed(true);
            if (deposit.getTenantConfirmed() != null && deposit.getTenantConfirmed()) {
                deposit.setStatus(DepositStatus.CONFIRMED);
            }
        } else {
            deposit.setStatus(DepositStatus.CANCELLED);
            deposit.setCancellationReason("Chủ trọ từ chối đơn đặt cọc");
        }

        deposit.setUpdatedAt(LocalDateTime.now());
        return depositRepository.save(deposit);
    }

    private String processDepositSuccess(String transactionCode, String amountStr, String orderInfo) {
        String[] parts = orderInfo.split(" - DepositID: ");
        Integer depositId = null;
        if (parts.length == 2) {
            try {
                depositId = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return "Không tìm thấy thông tin DepositID trong orderInfo.";
            }
        } else {
            return "Định dạng orderInfo không hợp lệ.";
        }

        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt cọc"));

        deposit.setStatus(DepositStatus.PAID);
        deposit.setTransactionId(transactionCode);
        deposit.setUpdatedAt(LocalDateTime.now());
        depositRepository.save(deposit);

        return "Đặt cọc thành công. Vui lòng chờ chủ trọ xác nhận.";
    }

    private String processDepositFailed(String transactionCode, String responseCode, String orderInfo) {
        String[] parts = orderInfo.split(" - DepositID: ");
        if (parts.length == 2) {
            try {
                Integer depositId = Integer.parseInt(parts[1]);
                Deposit deposit = depositRepository.findById(depositId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt cọc"));

                deposit.setStatus(DepositStatus.CANCELLED);
                deposit.setCancellationReason("Thanh toán thất bại: " + responseCode);
                deposit.setUpdatedAt(LocalDateTime.now());
                depositRepository.save(deposit);
            } catch (NumberFormatException e) {
                return "Không tìm thấy thông tin DepositID hợp lệ.";
            }
        }
        return "Giao dịch thanh toán thất bại.";
    }
} 