package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.DTO.response.UserLandlordResponse;
import com.example.nhatrobackend.DTO.response.UserPostCountDTO;
import com.example.nhatrobackend.DTO.response.UserProfileDTO;
import com.example.nhatrobackend.DTO.response.UserStatsResponse;
import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    boolean getApprovedUserByUuid(String userUuid);
    User findByUserUuid(String userUuid);
    User getUserByUuid(String userUuid);
    UserInformationDTO getUserInformationByUuid(String userUuid);
    UserProfileDTO getUserProfile(String userUuid);
    String registerLandlord(String userUuid, LandlordRegistrationDTO dto);
    UpdateUserDTO updateUser(String userUuid, UpdateUserDTO updateUserDTO);
    UpdateUserDTO getUserInfo(String userUuid);
    Page<UserAdminDTO> getUsersByStatus(LandlordStatus status, Pageable pageable);
    String getLandlordStatusByUserUuid(String userUuid);
    String updateProfilePicture(String userUuid, MultipartFile file) throws IOException;
    UserDetailAdminDTO getUserDetailById(Integer userId);
    UserDetailAdminDTO approveLandlord(Integer userId);
    UserDetailAdminDTO rejectLandlord(Integer userId);
    Page<UserAdminDTO> getAllUsersExcludingModerator(Pageable pageable);

    UserDetailsService userDetailsService();
    User findByPhoneNumber(String phoneNumber);
    User getUserByEmail(String email);
    Integer saveUser(User user);
    Page<UserLandlordResponse> getApprovedLandlords(Pageable pageable, String loggedInUserUuid);
    User findByUserId(Integer userId);

    User save(User user);
    UserStatsResponse getUserTypeCounts();

    UserPostCountDTO getUserPostCount(Integer userId);

    List<String> getFollowerEmails(Integer userId);

    String uploadCccdImages(String userUuid, MultipartFile frontCccd, MultipartFile backCccd) throws IOException;
}
