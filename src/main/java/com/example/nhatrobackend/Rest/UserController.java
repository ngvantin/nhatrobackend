package com.example.nhatrobackend.Rest;

import com.example.nhatrobackend.DTO.LandlordRegistrationDTO;
import com.example.nhatrobackend.DTO.ResponseWrapper;
import com.example.nhatrobackend.DTO.UserInformationDTO;
import com.example.nhatrobackend.DTO.UserProfileDTO;
import com.example.nhatrobackend.Sercurity.AuthenticationFacade;
import com.example.nhatrobackend.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        String userUuid = authenticationFacade.getCurrentUserUuid(request);

        // Gọi service để lấy thông tin
        UserInformationDTO userInformationDTO = userService.getUserInformationByUuid(userUuid);

        return ResponseEntity.ok(ResponseWrapper.<UserInformationDTO>builder()
                .status("success")
                .data(userInformationDTO)
                .message("Thông tin người dùng đang đăng nhập")
                .build());
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseWrapper<UserProfileDTO>> getUserProfile(HttpServletRequest request) {
        // Lấy userUuid từ JWT token trong cookie
        String userUuid = authenticationFacade.getCurrentUserUuid(request);

        // Gọi service để lấy thông tin người dùng
        UserProfileDTO userProfile = userService.getUserProfile(userUuid);

        return ResponseEntity.ok(ResponseWrapper.<UserProfileDTO>builder()
                .status("success")
                .message("Lấy thông tin cá nhân thành công")
                .data(userProfile)
                .build());
    }

    @PostMapping("/register-landlord")
    public ResponseEntity<ResponseWrapper<String>> registerLandlord(
            @RequestBody LandlordRegistrationDTO dto,
            HttpServletRequest request) {
        // Lấy userUuid từ JWT token trong cookie
        String userUuid = authenticationFacade.getCurrentUserUuid(request);

        // Gọi service để xử lý đăng ký
        String message = userService.registerLandlord(userUuid, dto);

        return ResponseEntity.ok(ResponseWrapper.<String>builder()
                .status("success")
                .message(message)
                .build());
    }
}


