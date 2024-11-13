package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.UserDetailDTO;
import com.example.nhatrobackend.Entity.User;

public interface UserService {
    boolean getApprovedUserByUuid(String userUuid);
    User findByUserUuid(String userUuid);
}
