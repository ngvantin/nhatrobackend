package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.*;
//import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.DTO.response.UserLandlordResponse;
import com.example.nhatrobackend.DTO.response.UserProfileDTO;
import com.example.nhatrobackend.DTO.response.UserStatsResponse;
import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Entity.Field.UserType;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Mapper.UserMapper;
//import com.example.nhatrobackend.Responsitory.AccountRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
//import com.example.nhatrobackend.Service.AccountService;
import com.example.nhatrobackend.Service.UploadImageFileService;
import com.example.nhatrobackend.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
//    private final AccountService accountService;
    private final UploadImageFileService uploadImageFileService;

//    private final AccountRepository accountRepository; // // dính lỗi khi sửa security xóa bảng account


    @Override
    public Page<UserLandlordResponse> getApprovedLandlords(Pageable pageable, String loggedInUserUuid) { // Thêm tham số loggedInUserUuid
        Page<User> userPage = userRepository.findByIsLandlordActivated(LandlordStatus.APPROVED, pageable);
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
        Page<User> userPage = userRepository.findByIsLandlordActivated(status, pageable);

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
        Page<User> userPage = userRepository.findByIsLandlordActivatedNot(LandlordStatus.MODERATOR, pageable);

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
    public UserDetailAdminDTO approveLandlord(Integer userId) {
//        User user = findUserByIdOrThrow(userId);
//
//        // Kiểm tra trạng thái hiện tại
//        if (user.getIsLandlordActivated() == LandlordStatus.APPROVED) {
//            throw new IllegalStateException("User is already approved as landlord.");
//        }
//
//        // Cập nhật trạng thái thành APPROVED
//        user.setIsLandlordActivated(LandlordStatus.APPROVED);
//        user.setUpdatedAt(LocalDateTime.now());
//
//        // Lưu vào DB
//        userRepository.save(user);
//        Account account = accountService.findAccountByUserId(userId);
//        account.setUserType(UserType.LANDLORD);
//        accountRepository.save(account);
//        // Trả về DTO
//        return userMapper.toUserDetailAdminDTO(user);
        return null;
    }

    @Override
    public UserDetailAdminDTO rejectLandlord(Integer userId) {
        User user = findUserByIdOrThrow(userId);

        // Cập nhật trạng thái thành NOT_REGISTERED
        user.setIsLandlordActivated(LandlordStatus.REJECTED);
        user.setUpdatedAt(LocalDateTime.now());

        // Lưu vào DB
        userRepository.save(user);

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
        long landlordCount = userRepository.countByType(UserType.LANDLORD);
        long tenantCount = userRepository.countByType(UserType.TENANT);
        long totalLandlordTenant = userRepository.countAllLandlordAndTenant();

        UserStatsResponse response = new UserStatsResponse();
        response.setLandlordCount(landlordCount);
        response.setTenantCount(tenantCount);
        response.setTotalLandlordTenant(totalLandlordTenant);

        return response;
    }
}
