package com.example.nhatrobackend.DTO.response;

import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Entity.Field.MessageStatus;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private String fullName;
    private String profilePicture;
    private String address;
    private LocalDateTime createdAt;
    private LandlordStatus isLandlordActivated;
    private Integer userId;
    private String userUuid = UUID.randomUUID().toString();
    private MessageStatus isOnline;
}

