package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.response.UserLandlordResponse;
import com.example.nhatrobackend.DTO.response.UserProfileDTO;
import com.example.nhatrobackend.DTO.response.UserStatsResponse;
import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;

    @GetMapping("/info")
    public ResponseEntity<ResponseWrapper<UserInformationDTO>> getCurrentUserInformation(HttpServletRequest request) {
        //        String userUuid = authenticationFacade.getCurrentUserUuid();
        // Lấy userUuid từ JWT token trong cookie
        String userUuid = authenticationFacade.getCurrentUserUuid();
//        log.infor("Roles: ");
        // Gọi service để lấy thông tin
        UserInformationDTO userInformationDTO = userService.getUserInformationByUuid(userUuid);

        return ResponseEntity.ok(ResponseWrapper.<UserInformationDTO>builder()
                .status("success")
                .data(userInformationDTO)
                .message("Thông tin người dùng đang đăng nhập")
                .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> getUserProfile() {
        // Lấy userUuid từ JWT token trong cookie
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Gọi service để lấy thông tin người dùng
        UserProfileDTO userProfile = userService.getUserProfile(userUuid);

        return ResponseEntity.ok(ResponseWrapper.<UserProfileDTO>builder()
                .status("success")
                .message("Lấy thông tin cá nhân thành công")
                .data(userProfile)
                .build());
    }

    // API thông tin 1 user bất kì
    @GetMapping("/{userUuid}")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> getUserProfileByUuid(
            @PathVariable String userUuid) {

        // Gọi service để lấy thông tin người dùng
        UserProfileDTO userProfile = userService.getUserProfile(userUuid);

        return ResponseEntity.ok(ResponseWrapper.<UserProfileDTO>builder()
                .status("success")
                .message("Lấy thông tin người dùng thành công")
                .data(userProfile)
                .build());
    }

    @PostMapping("/register-landlord")
    public ResponseEntity<ResponseWrapper<String>> registerLandlord(
            @RequestBody LandlordRegistrationDTO dto,
            HttpServletRequest request) {
        // Lấy userUuid từ JWT token trong cookie
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Gọi service để xử lý đăng ký
        String message = userService.registerLandlord(userUuid, dto);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message(message)
                .build());
    }

    // Endpoint lấy thông tin người dùng hiện tại
    @GetMapping("/info-update")
    public ResponseEntity<ResponseWrapper<UpdateUserDTO>> getCurrentUserInfo(HttpServletRequest request) {
        // Lấy userUuid từ cookie
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Gọi service để lấy thông tin người dùng
        UpdateUserDTO currentUserDTO = userService.getUserInfo(userUuid);

        return ResponseEntity.ok(ResponseWrapper.<UpdateUserDTO>builder()
                .status("success")
                .message("Lấy thông tin cá nhân thành công")
                .data(currentUserDTO)
                .build());
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseWrapper<UpdateUserDTO>> updateUser(HttpServletRequest request,
                                                    @RequestBody UpdateUserDTO updateUserDTO) {
        // Lấy userUuid từ cookie
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Gọi service để cập nhật thông tin người dùng
        UpdateUserDTO updatedUserDTO = userService.updateUser(userUuid, updateUserDTO);

        return ResponseEntity.ok(ResponseWrapper.<UpdateUserDTO>builder()
                .status("success")
                .message("Lấy thông tin cá nhân thành công")
                .data(updatedUserDTO)
                .build());
    }

    @GetMapping("/admin/not-registered")
    public ResponseEntity<ResponseWrapper<Page<UserAdminDTO>>> getNotRegisteredUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách người dùng với trạng thái NOT_REGISTERED
        Page<UserAdminDTO> users = userService.getUsersByStatus(LandlordStatus.NOT_REGISTERED, pageable);

        return ResponseEntity.ok(new ResponseWrapper<>("success", "Lấy danh sách người dùng chưa đăng ký thành công", users));
    }

    @GetMapping("/admin/pending-approval")
    public ResponseEntity<ResponseWrapper<Page<UserAdminDTO>>> getPendingApprovalUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách người dùng với trạng thái PENDING_APPROVAL
        Page<UserAdminDTO> users = userService.getUsersByStatus(LandlordStatus.PENDING_APPROVAL, pageable);

        return ResponseEntity.ok(new ResponseWrapper<>("success", "Lấy danh sách người dùng chờ xét duyệt thành công", users));
    }

    @GetMapping("/admin/approved")
    public ResponseEntity<ResponseWrapper<Page<UserAdminDTO>>> getApprovedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách người dùng với trạng thái APPROVED
        Page<UserAdminDTO> users = userService.getUsersByStatus(LandlordStatus.APPROVED, pageable);

        return ResponseEntity.ok(new ResponseWrapper<>("success", "Lấy danh sách người dùng đã được phê duyệt thành công", users));
    }
    @GetMapping("/admin/rejected")
    public ResponseEntity<ResponseWrapper<Page<UserAdminDTO>>> getRejectedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách người dùng với trạng thái APPROVED
        Page<UserAdminDTO> users = userService.getUsersByStatus(LandlordStatus.REJECTED, pageable);

        return ResponseEntity.ok(new ResponseWrapper<>("success", "Lấy danh sách người dùng bị từ chối", users));
    }
    @GetMapping("/admin/moderator")
    public ResponseEntity<ResponseWrapper<Page<UserAdminDTO>>> getAllModerators(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách người dùng với trạng thái APPROVED
        Page<UserAdminDTO> users = userService.getUsersByStatus(LandlordStatus.MODERATOR, pageable);

        return ResponseEntity.ok(new ResponseWrapper<>("success", "Lấy danh sách các quản trị viên thành công", users));
    }


    @GetMapping("/landlord-status")
    public ResponseEntity<ResponseWrapper<String>> getLandlordStatus(HttpServletRequest request) {
        // Lấy userUuid từ JWT token thông qua AuthenticationFacade
        String userUuid = authenticationFacade.getCurrentUserUuid();

        // Gọi service để lấy trạng thái của user và trả về dạng String
        String status = userService.getLandlordStatusByUserUuid(userUuid);

        return  ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message("Lấy trạng thái thành công.")
                .data(status)
                .build());
    }

    @GetMapping("/admin/all-users")
    public ResponseEntity<ResponseWrapper<Page<UserAdminDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // Gọi service để lấy danh sách người dùng
        Page<UserAdminDTO> users = userService.getAllUsersExcludingModerator(pageable);

        return ResponseEntity.ok(new ResponseWrapper<>("success", "Lấy danh sách người dùng thành công", users));
    }


    @PostMapping("/profile-picture")
    public ResponseEntity<ResponseWrapper<String>> updateProfilePicture(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        try {
            // Lấy userUuid từ JWT token trong cookie
            String userUuid = authenticationFacade.getCurrentUserUuid();

            // Gửi file và userUuid tới Service để xử lý
            String imageUrl = userService.updateProfilePicture(userUuid, file);

            // Trả về phản hồi thành công
            return ResponseEntity.ok(
                    ResponseWrapper.<String>builder()
                            .status("success")
                            .message("Cập nhật ảnh đại diện thành công.")
                            .data(imageUrl)
                            .build()
            );
        } catch (Exception e) {
            // Trả về lỗi nếu gặp vấn đề
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ResponseWrapper.<String>builder()
                                    .status("error")
                                    .message("Lỗi khi cập nhật ảnh đại diện: " + e.getMessage())
                                    .build()
                    );
        }
    }

    @GetMapping("/admin/detail/{userId}")
    public ResponseEntity<ResponseWrapper<UserDetailAdminDTO>> getUserDetail(@PathVariable Integer userId) {
        UserDetailAdminDTO userDetail = userService.getUserDetailById(userId);
        return  ResponseEntity.ok(ResponseWrapper.<UserDetailAdminDTO>builder()
                .status("success")
                .message("Lấy thông tin user thành công.")
                .data(userDetail)
                .build());
    }

    @PutMapping("/admin/approve-landlord/{userId}")
    public ResponseEntity<ResponseWrapper<UserDetailAdminDTO>> approveLandlord(@PathVariable Integer userId) {
        UserDetailAdminDTO userDetail = userService.approveLandlord(userId);
        return ResponseEntity.ok(ResponseWrapper.<UserDetailAdminDTO>builder()
                .status("success")
                .data(userDetail)
                .message("Quyền chủ trọ đã được duyệt.")
                .build());
    }

    @PutMapping("/admin/reject-landlord/{userId}")
    public ResponseEntity<ResponseWrapper<UserDetailAdminDTO>> rejectLandlord(@PathVariable Integer userId) {
        UserDetailAdminDTO userDetail = userService.rejectLandlord(userId);
        return ResponseEntity.ok(ResponseWrapper.<UserDetailAdminDTO>builder()
                .status("success")
                .data(userDetail)
                .message("Quyền chủ trọ đã bị từ chối.")
                .build());
    }


    @GetMapping("/landlords")
    public ResponseEntity<ResponseWrapper<Page<UserLandlordResponse>>> getApprovedLandlords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        String loggedInUserUuid = authenticationFacade.getCurrentUserUuidToPhone(); // Lấy userUuid từ AuthenticationFacade

        Page<UserLandlordResponse> landlords = userService.getApprovedLandlords(pageable, loggedInUserUuid); // Truyền loggedInUserUuid vào service

        return ResponseEntity.ok(new ResponseWrapper<>("success", "Lấy danh sách chủ trọ đã được phê duyệt thành công", landlords));
    }

    @GetMapping("/type-counts")
    public ResponseEntity<ResponseWrapper<UserStatsResponse>> getUserTypeCounts() {
        UserStatsResponse stats = userService.getUserTypeCounts();
        return ResponseEntity.ok(ResponseWrapper.<UserStatsResponse>builder()
                .status("success")
                .data(stats)
                .message("Thống kê số lượng người dùng theo loại.")
                .build());
    }
}


