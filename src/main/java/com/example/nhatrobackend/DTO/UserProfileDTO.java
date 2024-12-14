package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Entity.Field.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private String fullName;
    private String profilePicture;
    private String address;
    private LocalDateTime createdAt;
    private LandlordStatus isLandlordActivated;
}

