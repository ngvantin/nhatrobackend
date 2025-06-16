package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.Config.VNPAYConfig;
import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.request.DepositRefundRequest;
import com.example.nhatrobackend.DTO.request.DepositRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
import com.example.nhatrobackend.Entity.*;
import com.example.nhatrobackend.Entity.Field.DepositStatus;
import com.example.nhatrobackend.Entity.Field.PaymentType;
import com.example.nhatrobackend.Mapper.PostMapper;
import com.example.nhatrobackend.Responsitory.DepositRepository;
import com.example.nhatrobackend.Responsitory.DepositTenantComplaintImageRepository;
import com.example.nhatrobackend.Responsitory.DepositLandlordComplaintImageRepository;
import com.example.nhatrobackend.Responsitory.PostRepository;
import com.example.nhatrobackend.Service.DepositService;
import com.example.nhatrobackend.Service.NotificationService;
import com.example.nhatrobackend.Service.UploadImageFileService;
import com.example.nhatrobackend.Service.UserService;
import com.example.nhatrobackend.Service.MailService;
import com.example.nhatrobackend.Service.PaymentHistoryService;
import com.example.nhatrobackend.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {
    private final VNPAYConfig vnPayConfig;
    private final UserService userService;
    private final PostRepository postRepository;
    private final DepositRepository depositRepository;
    private final PostMapper postMapper;
    private final NotificationService notificationService;
    private final UploadImageFileService uploadImageFileService;
    private final DepositTenantComplaintImageRepository tenantComplaintImageRepository;
    private final DepositLandlordComplaintImageRepository landlordComplaintImageRepository;
    private final MailService mailService;
    private final PaymentHistoryService paymentHistoryService;
    private static final Logger log = LoggerFactory.getLogger(DepositServiceImpl.class);


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
        // Tìm deposit từ orderInfo
        String depositIdStr = orderInfo.split("DepositID: ")[1];
        int depositId = Integer.parseInt(depositIdStr);
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đặt cọc"));

        // Cập nhật trạng thái deposit
        deposit.setStatus(DepositStatus.PAID);
        deposit.setTransactionId(transactionCode);
        deposit.setUpdatedAt(LocalDateTime.now());
        depositRepository.save(deposit);

        // Gửi email thông báo
        try {
            mailService.sendDepositSuccessNotification(deposit);
        } catch (Exception e) {
            log.error("Failed to send deposit success notification email", e);
        }

        return "Đặt cọc thành công";
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

    @Override
    public Page<PostWithDepositDTO> getDepositedPosts(Integer userId, Pageable pageable) {
        Page<Deposit> deposits = depositRepository.findByUser_UserId(userId, pageable);
        return deposits.map(deposit -> postMapper.toPostWithDepositDTO(deposit.getPost(), deposit));
    }

    @Override
    public Page<PostResponseDTO> getPostsWithDepositsByOtherUsers(Integer currentUserId, Pageable pageable) {
        // Get all posts that have deposits from other users
        Page<Post> posts = depositRepository.findPostsWithDepositsByOtherUsers(currentUserId, pageable);

        // Convert to DTO using MapStruct
        return posts.map(postMapper::toPostResponseDTO);
    }

    @Override
    public List<UserDepositDTO> getUsersWithDepositsByPostId(Integer postId) {
        List<Deposit> deposits = depositRepository.findByPost_PostId(postId);
        return deposits.stream()
                .map(deposit -> UserDepositDTO.builder()
                        .depositId(deposit.getDepositId())
                        .postId(postId)
                        .postUuid(deposit.getPost().getPostUuid())
                        .userId(deposit.getUser().getUserId())
                        .userUuid(deposit.getUser().getUserUuid())
                        .fullName(deposit.getUser().getFullName())
                        .profilePicture(deposit.getUser().getProfilePicture())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public DepositDetailDTO getDepositDetailsById(Integer depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt cọc với ID: " + depositId));

        User depositor = deposit.getUser();
        User landlord = deposit.getPost().getUser();

        return DepositDetailDTO.builder()
                .depositId(deposit.getDepositId())
                .createdAt(deposit.getCreatedAt())
                // Thông tin người đặt cọc
                .depositorId(depositor.getUserId())
                .depositorFullName(depositor.getFullName())
                .depositorEmail(depositor.getEmail())
                .depositorPhoneNumber(depositor.getPhoneNumber())
                // Thông tin chủ trọ
                .landlordId(landlord.getUserId())
                .landlordFullName(landlord.getFullName())
                .landlordEmail(landlord.getEmail())
                .landlordPhoneNumber(landlord.getPhoneNumber())
                // Trạng thái xác nhận
                .landlordConfirmed(deposit.getLandlordConfirmed())
                .tenantConfirmed(deposit.getTenantConfirmed())
                .build();
    }

    @Override
    public String confirmByTenant(Integer depositId, Integer currentUserId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt cọc với ID: " + depositId));

        // Kiểm tra xem người dùng hiện tại có phải là người đặt cọc không
        if (!deposit.getUser().getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("Bạn không có quyền xác nhận đơn đặt cọc này");
        }

        // Cập nhật trạng thái xác nhận từ người thuê
        deposit.setTenantConfirmed(true);
        deposit.setUpdatedAt(LocalDateTime.now());

        // Nếu cả hai bên đã xác nhận, cập nhật trạng thái đặt cọc
        if (deposit.getLandlordConfirmed() != null && deposit.getLandlordConfirmed()) {
            deposit.setStatus(DepositStatus.CONFIRMED);
        }

        depositRepository.save(deposit);
        return "Xác nhận đặt cọc từ người thuê thành công.";
    }

    @Override
    public String confirmByLandlord(Integer depositId, Integer currentUserId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt cọc với ID: " + depositId));

        // Kiểm tra xem người dùng hiện tại có phải là chủ trọ không
        if (!deposit.getPost().getUser().getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("Bạn không có quyền xác nhận đơn đặt cọc này");
        }

        // Cập nhật trạng thái xác nhận từ chủ trọ
        deposit.setLandlordConfirmed(true);
        deposit.setUpdatedAt(LocalDateTime.now());

        // Nếu cả hai bên đã xác nhận, cập nhật trạng thái đặt cọc
        if (deposit.getTenantConfirmed() != null && deposit.getTenantConfirmed()) {
            deposit.setStatus(DepositStatus.CONFIRMED);
        }

        depositRepository.save(deposit);
        return "Xác nhận đặt cọc từ chủ trọ thành công.";
    }

    @Override
    @Transactional
    public String complaintByTenant(Integer depositId, Integer currentUserId, DepositComplaintRequestDTO requestDTO) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt cọc"));

        // Kiểm tra xem người dùng hiện tại có phải là người đặt cọc không
        if (!deposit.getUser().getUserId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền khiếu nại đặt cọc này");
        }

        // Cập nhật thông tin khiếu nại
        deposit.setTenantComplaintReason(requestDTO.getReason());
        deposit.setUpdatedAt(LocalDateTime.now());
        deposit.setTenantConfirmed(false);

        // Upload video nếu có
        if (requestDTO.getVideo() != null && !requestDTO.getVideo().isEmpty()) {
            try {
                String videoUrl = uploadImageFileService.uploadImage(requestDTO.getVideo());
                deposit.setTenantComplaintVideoUrl(videoUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi upload video khiếu nại", e);
            }
        }

        // Lưu deposit để có ID
        Deposit savedDeposit = depositRepository.save(deposit);

        // Upload và lưu ảnh nếu có
        if (requestDTO.getImages() != null && !requestDTO.getImages().isEmpty()) {
            List<DepositTenantComplaintImage> complaintImages = new ArrayList<>();
            for (MultipartFile imageFile : requestDTO.getImages()) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    try {
                        String imageUrl = uploadImageFileService.uploadImage(imageFile);
                        DepositTenantComplaintImage complaintImage = new DepositTenantComplaintImage(imageUrl, savedDeposit);
                        complaintImages.add(complaintImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi khi upload ảnh khiếu nại", e);
                    }
                }
            }
            tenantComplaintImageRepository.saveAll(complaintImages);
        }

        return "Gửi khiếu nại từ người thuê thành công.";
    }

    @Override
    @Transactional
    public String complaintByLandlord(Integer depositId, Integer currentUserId, DepositComplaintRequestDTO requestDTO) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt cọc"));

        // Kiểm tra xem người dùng hiện tại có phải là chủ trọ không
        if (!deposit.getPost().getUser().getUserId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền khiếu nại đặt cọc này");
        }

        // Cập nhật thông tin khiếu nại
        deposit.setLandlordComplaintReason(requestDTO.getReason());
        deposit.setUpdatedAt(LocalDateTime.now());
        deposit.setLandlordConfirmed(false);

        // Upload video nếu có
        if (requestDTO.getVideo() != null && !requestDTO.getVideo().isEmpty()) {
            try {
                String videoUrl = uploadImageFileService.uploadImage(requestDTO.getVideo());
                deposit.setLandlordComplaintVideoUrl(videoUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi upload video khiếu nại", e);
            }
        }

        // Lưu deposit để có ID
        Deposit savedDeposit = depositRepository.save(deposit);

        // Upload và lưu ảnh nếu có
        if (requestDTO.getImages() != null && !requestDTO.getImages().isEmpty()) {
            List<DepositLandlordComplaintImage> complaintImages = new ArrayList<>();
            for (MultipartFile imageFile : requestDTO.getImages()) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    try {
                        String imageUrl = uploadImageFileService.uploadImage(imageFile);
                        DepositLandlordComplaintImage complaintImage = new DepositLandlordComplaintImage(imageUrl, savedDeposit);
                        complaintImages.add(complaintImage);
                    } catch (IOException e) {
                        throw new RuntimeException("Lỗi khi upload ảnh khiếu nại", e);
                    }
                }
            }
            landlordComplaintImageRepository.saveAll(complaintImages);
        }

        return "Gửi khiếu nại từ chủ trọ thành công.";
    }

    @Override
    public DepositFullDetailDTO getFullDepositDetails(Integer depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt cọc với ID: " + depositId));

        User depositor = deposit.getUser();
        User landlord = deposit.getPost().getUser();

        return DepositFullDetailDTO.builder()
                .depositId(deposit.getDepositId())
                .status(deposit.getStatus())
                .landlordConfirmed(deposit.getLandlordConfirmed())
                .tenantConfirmed(deposit.getTenantConfirmed())
                .tenantComplaintReason(deposit.getTenantComplaintReason())
                .tenantComplaintVideoUrl(deposit.getTenantComplaintVideoUrl())
                .landlordComplaintReason(deposit.getLandlordComplaintReason())
                .landlordComplaintVideoUrl(deposit.getLandlordComplaintVideoUrl())
                .tenantComplaintImages(deposit.getTenantComplaintImages() != null ? 
                    deposit.getTenantComplaintImages().stream()
                        .map(DepositTenantComplaintImage::getImageUrl)
                        .toList() : null)
                .landlordComplaintImages(deposit.getLandlordComplaintImages() != null ? 
                    deposit.getLandlordComplaintImages().stream()
                        .map(DepositLandlordComplaintImage::getImageUrl)
                        .toList() : null)
                .createdAt(deposit.getCreatedAt())
                // Thông tin người đặt cọc
                .depositorId(depositor.getUserId())
                .depositorFullName(depositor.getFullName())
                .depositorEmail(depositor.getEmail())
                .depositorPhoneNumber(depositor.getPhoneNumber())
                // Thông tin chủ trọ
                .landlordId(landlord.getUserId())
                .landlordFullName(landlord.getFullName())
                .landlordEmail(landlord.getEmail())
                .landlordPhoneNumber(landlord.getPhoneNumber())
                .build();
    }

    @Override
    public Page<DepositStatusDTO> getDepositsByStatus(DepositStatus status, Pageable pageable) {
        Page<Deposit> deposits = depositRepository.findByStatus(status, pageable);
        return deposits.map(deposit -> DepositStatusDTO.builder()
                .depositId(deposit.getDepositId())
                .postUuid(deposit.getPost().getPostUuid())
                .amount(deposit.getAmount())
                .paymentMethod(deposit.getPaymentMethod())
                .transactionId(deposit.getTransactionId())
                .build());
    }

    @Override
    @Transactional
    public String refundDeposit(DepositRefundRequest request, HttpServletRequest httpRequest) {
        Deposit deposit = depositRepository.findById(request.getDepositId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt cọc"));

        // Kiểm tra trạng thái deposit
        if (deposit.getStatus() != DepositStatus.PAID && deposit.getStatus() != DepositStatus.CONFIRMED) {
            throw new RuntimeException("Không thể hoàn tiền cho đơn đặt cọc này");
        }

        // Chuẩn bị thông tin hoàn tiền
        String transactionId = deposit.getTransactionId();
        String transactionDate = deposit.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Double amount = deposit.getAmount();
        String orderInfo = "Hoan tien dat coc - DepositID: " + deposit.getDepositId();
        String ipAddress = VNPayUtil.getIpAddress(httpRequest);

        log.info("Refund information:");
        log.info("Transaction ID: {}", transactionId);
        log.info("Transaction Date: {}", transactionDate);
        log.info("Amount: {}", amount);
        log.info("Order Info: {}", orderInfo);
        log.info("IP Address: {}", ipAddress);

        // Tạo cấu hình hoàn tiền
        Map<String, String> vnpParams = vnPayConfig.getVNPayRefundConfig(
                transactionId, transactionDate, amount, orderInfo, ipAddress);

        // Tạo chuỗi hash
        String hashData = String.join("|",
                vnpParams.get("vnp_RequestId"),
                vnpParams.get("vnp_Version"),
                vnpParams.get("vnp_Command"),
                vnpParams.get("vnp_TmnCode"),
                vnpParams.get("vnp_TransactionType"),
                vnpParams.get("vnp_TxnRef"),
                vnpParams.get("vnp_Amount"),
                vnpParams.get("vnp_TransactionDate"),
                vnpParams.get("vnp_CreateBy"),
                vnpParams.get("vnp_CreateDate"),
                vnpParams.get("vnp_IpAddr"),
                vnpParams.get("vnp_OrderInfo")
        );

        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        vnpParams.put("vnp_SecureHash", vnpSecureHash);

        log.info("VNPAY Parameters:");
        vnpParams.forEach((key, value) -> log.info("{}: {}", key, value));
        log.info("Hash Data: {}", hashData);
        log.info("Secure Hash: {}", vnpSecureHash);

        // Gọi API hoàn tiền
        try {
            log.info("Calling VNPAY API at URL: {}", vnPayConfig.getVnp_RefundUrl());
            String response = VNPayUtil.callVNPayAPI(vnPayConfig.getVnp_RefundUrl(), vnpParams);
            log.info("VNPAY Response: {}", response);
            
            // Parse response và cập nhật trạng thái
            if (response.contains("\"vnp_ResponseCode\":\"00\"")) {
                deposit.setStatus(DepositStatus.REFUNDED);
                deposit.setRefundAmount(amount);
                deposit.setRefundTransactionId(vnpParams.get("vnp_RequestId"));
                deposit.setUpdatedAt(LocalDateTime.now());
                depositRepository.save(deposit);
                return "Hoàn tiền thành công";
            } else {
                throw new RuntimeException("Hoàn tiền thất bại: " + response);
            }
        } catch (Exception e) {
            log.error("Error calling VNPAY API", e);
            throw new RuntimeException("Lỗi khi gọi API hoàn tiền: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public String refundDepositToTenant(Integer depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt cọc"));

        // Kiểm tra trạng thái đặt cọc
        if (deposit.getStatus() == DepositStatus.PAID) {
            throw new RuntimeException("Đơn đặt cọc chưa được thanh toán");
        }

        // Hoàn tiền cho người đặt cọc
        User tenant = deposit.getUser();
        tenant.setBalance(tenant.getBalance() + deposit.getAmount());
        userService.saveUser(tenant);

        // Cập nhật trạng thái đặt cọc
        deposit.setStatus(DepositStatus.REFUNDED);
        deposit.setRefundAmount(deposit.getAmount());
        deposit.setUpdatedAt(LocalDateTime.now());
        depositRepository.save(deposit);

        // Gửi email thông báo hoàn tiền
        try {
            mailService.sendRefundSuccessNotification(deposit);
        } catch (Exception e) {
            log.error("Failed to send refund notification email: {}", e.getMessage());
        }

        return "Hoàn tiền thành công";
    }

    @Override
    @Transactional
    public String payCommissionToLandlord(Integer depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt cọc"));

        // Kiểm tra trạng thái đặt cọc
        if (deposit.getStatus() == DepositStatus.PAID) {
            throw new RuntimeException("Đơn đặt cọc chưa được thanh toán");
        }

        Post post = deposit.getPost();
        Room room = post.getRoom();
        User landlord = post.getUser();

        // Tính toán số tiền hoa hồng (10% giá phòng)
        double commissionAmount = room.getPrice() * 0.1;
        double landlordAmount = deposit.getAmount() - commissionAmount;

        // Cập nhật số dư cho chủ trọ
        landlord.setBalance(landlord.getBalance() + landlordAmount);
        userService.saveUser(landlord);

        deposit.setStatus(DepositStatus.COMMISSION);
        depositRepository.save(deposit);

        // Gửi email thông báo thanh toán hoa hồng
        try {
            mailService.sendCommissionPaymentNotification(deposit, landlordAmount, commissionAmount);
        } catch (Exception e) {
            log.error("Failed to send commission payment notification email: {}", e.getMessage());
        }

        return "Thanh toán hoa hồng thành công";
    }


    @Override
    @Transactional
    public String paySuccessToLandlord(Integer depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt cọc"));

        // Kiểm tra trạng thái đặt cọc
        if (deposit.getStatus() == DepositStatus.PAID) {
            throw new RuntimeException("Đơn đặt cọc chưa được thanh toán");
        }

        Post post = deposit.getPost();
        Room room = post.getRoom();
        User landlord = post.getUser();

        // Tính toán số tiền hoa hồng (10% giá phòng)
        double commissionAmount = room.getPrice() * 0.1;
        double landlordAmount = deposit.getAmount() - commissionAmount;

        // Cập nhật số dư cho chủ trọ
        landlord.setBalance(landlord.getBalance() + landlordAmount);
        userService.saveUser(landlord);

        deposit.setStatus(DepositStatus.SUCCESS);
        depositRepository.save(deposit);

        // Gửi email thông báo thanh toán hoa hồng
        try {
            mailService.sendCommissionPaymentNotification(deposit, landlordAmount, commissionAmount);
        } catch (Exception e) {
            log.error("Failed to send commission payment notification email: {}", e.getMessage());
        }

        return "Thanh toán hoa hồng thành công";
    }
} 