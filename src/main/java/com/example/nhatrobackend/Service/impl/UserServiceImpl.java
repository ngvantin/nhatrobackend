package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.*;
//import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.DTO.response.NotificationResponse;
import com.example.nhatrobackend.DTO.response.UserLandlordResponse;
import com.example.nhatrobackend.DTO.response.UserPostCountDTO;
import com.example.nhatrobackend.DTO.response.UserProfileDTO;
import com.example.nhatrobackend.DTO.response.UserStatsResponse;
import com.example.nhatrobackend.Entity.Field.*;
import com.example.nhatrobackend.Entity.Notification;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.UserMapper;
//import com.example.nhatrobackend.Responsitory.AccountRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
//import com.example.nhatrobackend.Service.AccountService;
import com.example.nhatrobackend.Service.MailService;
import com.example.nhatrobackend.Service.NotificationService;
import com.example.nhatrobackend.Service.UploadImageFileService;
import com.example.nhatrobackend.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.example.nhatrobackend.Entity.Field.UserType.LANDLORD;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
//    private final AccountService accountService;
    private final UploadImageFileService uploadImageFileService;
    private final NotificationService notificationService;

    private final MailService mailService;
    @Value("${spring.application.serverName}")
    private String serverName;

//    private final AccountRepository accountRepository; // // dính lỗi khi sửa security xóa bảng account


    @Override
    public Page<UserLandlordResponse> getApprovedLandlords(Pageable pageable, String loggedInUserUuid) { // Thêm tham số loggedInUserUuid
        Page<User> userPage = userRepository.findByIsLandlordActivatedOrderByCreatedAtDesc(LandlordStatus.APPROVED, pageable);
        return userPage.map(user -> convertToUserLandlordResponse(user, loggedInUserUuid));
    }

    private UserLandlordResponse convertToUserLandlordResponse(User user, String loggedInUserUuid) {
        UserLandlordResponse dto = userMapper.toUserLandlordResponse(user);

        if (loggedInUserUuid == null ) {
            String phoneNumber = user.getPhoneNumber();
            if (phoneNumber != null && phoneNumber.length() > 3) {
                dto.setPhoneNumber(phoneNumber.substring(0, phoneNumber.length() - 3) + "xxx");
            }
        }
        return dto;
    }
    @Override
    public boolean getApprovedUserByUuid(String userUuid) {
        Optional<User> optionalUser = userRepository.findByUserUuid(userUuid);
        return optionalUser.isPresent() && optionalUser.get().getIsLandlordActivated() == LandlordStatus.APPROVED;
    }

    @Override
    public User findByUserUuid(String userUuid) {
        Optional<User> optionalUser = userRepository.findByUserUuid(userUuid);

        // Nếu tìm thấy Post, chuyển đổi sang PostDetailResponseDTO
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new EntityNotFoundException("Không tìm thấy User ID: " + userUuid);
        }
    }

    @Override
    public User getUserByUuid(String userUuid) {
        return userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found with UUID: " + userUuid));
    }

    @Override
    public User findByUserId(Integer userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with userId: " + userId));
    }

    // dính lỗi khi sửa security xóa bảng account
    @Override
    public UserInformationDTO getUserInformationByUuid(String userUuid) {
//        // Tìm userId từ userUuid
//        Integer userId = userRepository.findByUserUuid(userUuid)
//                .map(User::getUserId)
//                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với UUID đã cung cấp."));
//
//        // Gọi AccountService để lấy `Account`
//        Account account = accountService.findAccountByUserId(userId);
//        UserInformationDTO userInformationDTO = new UserInformationDTO();
//        userInformationDTO.setUserType(account.getUserType());
////        userInformationDTO.setFullName(account.getUser().getFullName());
//
//        return userInformationDTO;
        return null;
    }

    @Override
    public UserProfileDTO getUserProfile(String userUuid) {
        log.info("--fix---: " + userUuid);

        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Sử dụng MapStruct để chuyển đổi
        return userMapper.toUserProfileDTO(user);
    }

    @Override
    public String registerLandlord(String userUuid, LandlordRegistrationDTO dto) {
        // Lấy user từ database
        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra trạng thái hiện tại
        if (user.getIsLandlordActivated() == LandlordStatus.APPROVED) {
            throw new RuntimeException("Người dùng đã là chủ trọ");
        }

        // Kiểm tra trạng thái hiện tại
        if (user.getIsLandlordActivated() == LandlordStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Người dùng đã đăng ký. Hãy chờ xét duyệt.");
        }

        // Cập nhật thông tin CCCD và trạng thái
        userMapper.updateLandlordDetails(dto, user);
        user.setIsLandlordActivated(LandlordStatus.PENDING_APPROVAL);
        user.setUpdatedAt(LocalDateTime.now());

        // Lưu lại thay đổi
        userRepository.save(user);

        return "Đăng ký quyền chủ trọ thành công, vui lòng chờ phê duyệt.";
    }

    // Phương thức lấy thông tin người dùng hiện tại
    public UpdateUserDTO getUserInfo(String userUuid) {
        // Tìm người dùng trong cơ sở dữ liệu theo userUuid
        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Chuyển đổi từ User entity sang UpdateUserDTO
        return userMapper.toUpdateUserDTO(user);
    }

    @Override
    public UpdateUserDTO updateUser(String userUuid, UpdateUserDTO updateUserDTO) {
        Optional<User> optionalUser = userRepository.findByUserUuid(userUuid);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Người dùng không tồn tại");
        }

        // Lấy user từ cơ sở dữ liệu
        User user = optionalUser.get();

        // Cập nhật thông tin user từ DTO
        userMapper.updateUserFromDTO(updateUserDTO, user);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Chuyển đổi entity sang DTO để trả về
        return userMapper.toUpdateUserDTO(user);
    }

    @Override
    public Page<UserAdminDTO> getUsersByStatus(LandlordStatus status, Pageable pageable) {
        // Lấy danh sách người dùng theo trạng thái
        Page<User> userPage = userRepository.findByIsLandlordActivatedOrderByCreatedAtDesc(status, pageable);

        // Chuyển đổi danh sách User thành danh sách UserAdminDTO
        return userPage.map(this::convertToUserAdminDTO);
    }

    private UserAdminDTO convertToUserAdminDTO(User user) {
        // Sử dụng MapStruct để chuyển đổi từ User sang UserAdminDTO
        return userMapper.toUserAdminDTO(user);
    }

    @Override
    public Page<UserAdminDTO> getAllUsersExcludingModerator(Pageable pageable) {
        // Lấy danh sách người dùng, loại trừ trạng thái MODERATOR
        Page<User> userPage = userRepository.findByIsLandlordActivatedNotOrderByCreatedAtDesc(LandlordStatus.MODERATOR, pageable);

        // Chuyển đổi danh sách User sang UserAdminDTO
        return userPage.map(this::convertToUserAdminDTO);
    }

    @Override
    public String getLandlordStatusByUserUuid(String userUuid) {
        // Tìm user dựa vào userUuid
        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("User not found with UUID: " + userUuid));

        // Trả về trạng thái dưới dạng chuỗi
        return user.getIsLandlordActivated().name(); // Enum -> String
    }

    public String updateProfilePicture(String userUuid, MultipartFile file) throws IOException {
        // Tìm User trong database bằng userUuid
        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        // Upload file lên Cloudinary
        String imageUrl = uploadImageFileService.uploadImage(file);

        // Cập nhật URL ảnh đại diện trong database
        user.setProfilePicture(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }

    @Override
    public UserDetailAdminDTO getUserDetailById(Integer userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại với ID: " + userId));

        // Map User entity sang UserDetailAdminDTO
        return userMapper.toUserDetailAdminDTO(user);
    }

    private User findUserByIdOrThrow(Integer userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    // dính lỗi khi sửa security xóa bảng account
    @Override
    @Transactional
    public UserDetailAdminDTO approveLandlord(Integer userId) {
        try {
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

            // Kiểm tra trạng thái hiện tại
            if (user.getIsLandlordActivated() == LandlordStatus.APPROVED) {
                throw new IllegalStateException("Người dùng đã được duyệt quyền chủ trọ trước đó.");
            }

            // Cập nhật trạng thái thành APPROVED
            user.setIsLandlordActivated(LandlordStatus.APPROVED);
            user.setUpdatedAt(LocalDateTime.now());
            user.setType(LANDLORD);
            
            // Lưu vào DB
            User savedUser = userRepository.save(user);
            if (savedUser == null) {
                throw new RuntimeException("Không thể lưu thông tin người dùng");
            }

            // Tạo và lưu notification vào database
            Notification notification = Notification.builder()
                    .title("Quyền chủ trọ đã được duyệt")
                    .content("Chúng tôi xin thông báo rằng yêu cầu cấp quyền chủ trọ của bạn đã được chấp thuận.")
                    .type(EventType.LANDLORD_APPROVED.name())
                    .userId(userId)
                    .postId(null)
                    .redirectUrl("/posts/")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            // Lưu notification vào database
            Notification savedNotification = notificationService.save(notification);
            if (savedNotification == null) {
                log.warn("Không thể lưu notification cho user ID: {}", userId);
            }

            // Tạo NotificationResponse từ notification đã lưu
            NotificationResponse notificationResponse = NotificationResponse.builder()
                    .id(savedNotification.getId())
                    .title(savedNotification.getTitle())
                    .content(savedNotification.getContent())
                    .type(savedNotification.getType())
                    .userId(savedNotification.getUserId())
                    .postId(savedNotification.getPostId())
                    .createdAt(savedNotification.getCreatedAt())
                    .isRead(savedNotification.isRead())
                    .redirectUrl(savedNotification.getRedirectUrl())
                    .build();

            // Tạo và gửi notification event
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("postId", null);  // Use null directly since it's allowed in HashMap
            metadata.put("userId", userId);  // Use Integer directly

            NotificationEvent event = NotificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .type(EventType.LANDLORD_APPROVED)
                    .notification(notificationResponse)
                    .timestamp(LocalDateTime.now())
                    .metadata(metadata)
                    .priority(NotificationEvent.Priority.HIGH)
                    .status(Status.PENDING)
                    .build();

            // Gửi notification
            notificationService.sendNotification(event);

            // Log để debug
            log.info("User approved as landlord successfully. User ID: {}", userId);
            log.info("Notification saved to database with ID: {}", savedNotification.getId());
            log.info("Notification event sent: {}", event);

            // Gửi email thông báo
            try {
                String postUrl = String.format("%s/posts/%d", serverName, user.getUserId());
                mailService.sendLandlordApprovedNotification(user.getEmail());
            } catch (Exception e) {
                log.error("Failed to send post rejection email", e);
                // Không throw exception vì đây không phải là lỗi nghiêm trọng
            }

            // Chuyển đổi và trả về DTO
            UserDetailAdminDTO userDetailDTO = userMapper.toUserDetailAdminDTO(savedUser);
            if (userDetailDTO == null) {
                throw new RuntimeException("Không thể chuyển đổi thông tin người dùng sang DTO");
            }
            return userDetailDTO;

        } catch (Exception e) {
            log.error("Error in approveLandlord: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDetailAdminDTO rejectLandlord(Integer userId) {
        User user = findUserByIdOrThrow(userId);

        // Cập nhật trạng thái thành NOT_REGISTERED
        user.setIsLandlordActivated(LandlordStatus.REJECTED);
        user.setUpdatedAt(LocalDateTime.now());

        // Lưu vào DB
        userRepository.save(user);

        // Tạo và lưu notification vào database
        Notification notification = Notification.builder()
                .title("Quyền chủ trọ đã bị từ chối")
                .content("Chúng tôi xin thông báo rằng yêu cầu cấp quyền chủ trọ của bạn đã bị từ chối. Hãy kiểm tra lại thông tin và đăng ký chủ trọ lại")
                .type(EventType.POST_REPORTED.name())
                .userId(userId)
                .postId(null)
                .redirectUrl("/posts/")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Lưu notification vào database
        Notification savedNotification = notificationService.save(notification);


        // Tạo NotificationResponse từ notification đã lưu
        NotificationResponse notificationResponse = NotificationResponse.builder()
                .id(savedNotification.getId())
                .title(savedNotification.getTitle())
                .content(savedNotification.getContent())
                .type(savedNotification.getType())
                .userId(savedNotification.getUserId())
                .postId(savedNotification.getPostId())
                .createdAt(savedNotification.getCreatedAt())
                .isRead(savedNotification.isRead())
                .redirectUrl(savedNotification.getRedirectUrl())
                .build();

        // Tạo và gửi notification event
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("postId", null);  // Use null directly since it's allowed in HashMap
        metadata.put("userId", userId);  // Use Integer directly

        NotificationEvent event = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .type(EventType.LANDLORD_APPROVED)
                .notification(notificationResponse)
                .timestamp(LocalDateTime.now())
                .metadata(metadata)
                .priority(NotificationEvent.Priority.HIGH)
                .status(Status.PENDING)
                .build();

        // Gửi notification
        notificationService.sendNotification(event);

        // Log để debug
        log.info("Notification saved to database with ID: {}", savedNotification.getId());
        log.info("Notification event sent: {}", event);

        // Gửi email thông báo
        try {
            String postUrl = String.format("%s/posts/%d", serverName, user.getUserId());
            mailService.sendLandlordRejectedNotification(user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send post rejection email", e);
            // Không throw exception vì đây không phải là lỗi nghiêm trọng
        }
        // Trả về DTO
        return userMapper.toUserDetailAdminDTO(user);
    }

    @Override
    public UserDetailsService userDetailsService() {
        return phoneNumber -> userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User không tồn tại với phone number: " + phoneNumber));
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User không tồn tại với email: " + email));
    }

    @Override
    public Integer saveUser(User user) {
        userRepository.save(user);
        return user.getUserId();
    }
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserStatsResponse getUserTypeCounts() {
        long landlordCount = userRepository.countByType(LANDLORD);
        long tenantCount = userRepository.countByType(UserType.TENANT);
        long totalLandlordTenant = userRepository.countAllLandlordAndTenant();

        UserStatsResponse response = new UserStatsResponse();
        response.setLandlordCount(landlordCount);
        response.setTenantCount(tenantCount);
        response.setTotalLandlordTenant(totalLandlordTenant);

        return response;
    }

    @Override
    public UserPostCountDTO getUserPostCount(Integer userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        return UserPostCountDTO.builder()
                .userId(user.getUserId())
                .userUuid(user.getUserUuid())
                .fullName(user.getFullName())
                .postCount(user.getPostCount())
                .build();
    }

    /**
     * Lấy danh sách email của những người theo dõi một user
     * @param userId ID của user cần lấy danh sách người theo dõi
     * @return Danh sách email của những người theo dõi
     */
    @Override
    public List<String> getFollowerEmails(Integer userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        return user.getFollowers().stream()
                .map(follower -> follower.getFollowingUser().getEmail())
                .filter(email -> email != null && !email.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public String uploadCccdImages(String userUuid, MultipartFile frontCccd, MultipartFile backCccd, CccdUploadDTO dto) throws IOException {
        // Lấy user từ database
        User user = userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra trạng thái hiện tại
        if (user.getIsLandlordActivated() == LandlordStatus.APPROVED) {
            throw new RuntimeException("Người dùng đã là chủ trọ");
        }

        // Kiểm tra trạng thái hiện tại
        if (user.getIsLandlordActivated() == LandlordStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Người dùng đã đăng ký. Hãy chờ xét duyệt.");
        }

        // Kiểm tra số CCCD đã tồn tại chưa
        if (userRepository.findByCccdNumber(dto.getCccdNumber()).isPresent()) {
            throw new RuntimeException("Số CCCD đã được đăng ký bởi người dùng khác");
        }

        // Upload ảnh lên Cloudinary
        String frontCccdUrl = uploadImageFileService.uploadImage(frontCccd);
        String backCccdUrl = uploadImageFileService.uploadImage(backCccd);

        // Cập nhật thông tin user
        user.setFrontCccdUrl(frontCccdUrl);
        user.setBackCccdUrl(backCccdUrl);
        user.setIsLandlordActivated(LandlordStatus.PENDING_APPROVAL);
        user.setUpdatedAt(LocalDateTime.now());
        
        // Cập nhật thông tin CCCD
        user.setFullName(dto.getFullName());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setCccdNumber(dto.getCccdNumber());
        user.setGender(dto.getGender());
        user.setNationality(dto.getNationality());
        user.setHometown(dto.getHometown());
        user.setCccdIssueDate(dto.getCccdIssueDate());

        // Lưu lại thay đổi
        userRepository.save(user);

        return "Đăng ký quyền chủ trọ thành công, vui lòng chờ phê duyệt.";
    }

    @Override
    @Transactional
    public UserDetailAdminDTO lockUserAccount(Integer userId) {
        User user = findUserByIdOrThrow(userId);

        // Kiểm tra nếu user đã bị khóa
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new IllegalStateException("Tài khoản người dùng đã bị khóa trước đó.");
        }

        // Cập nhật trạng thái thành LOCKED
        user.setStatus(UserStatus.LOCKED);
        user.setUpdatedAt(LocalDateTime.now());

        // Lưu vào DB
        User savedUser = userRepository.save(user);

        // Gửi email thông báo
        try {
            mailService.sendAccountLockedNotification(user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send account locked email", e);
        }

        return userMapper.toUserDetailAdminDTO(savedUser);
    }
}
