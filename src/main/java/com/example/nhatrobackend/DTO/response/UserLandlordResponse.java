package com.example.nhatrobackend.DTO.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserLandlordResponse {
    private String userUuid;
    private String fullName;
    private String profilePicture;
    private String phoneNumber;
    private LocalDateTime createdAt;
}
