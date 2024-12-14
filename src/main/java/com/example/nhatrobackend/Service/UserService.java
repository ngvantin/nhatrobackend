package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.*;
import com.example.nhatrobackend.Entity.User;

import java.util.Optional;

public interface UserService {
    boolean getApprovedUserByUuid(String userUuid);
    User findByUserUuid(String userUuid);
    User getUserByUuid(String userUuid);
    UserInformationDTO getUserInformationByUuid(String userUuid);
    UserProfileDTO getUserProfile(String userUuid);
    String registerLandlord(String userUuid, LandlordRegistrationDTO dto);
    UpdateUserDTO updateUser(String userUuid, UpdateUserDTO updateUserDTO);

}
