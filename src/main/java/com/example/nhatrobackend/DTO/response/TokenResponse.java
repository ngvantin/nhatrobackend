package com.example.nhatrobackend.DTO.response;

import com.example.nhatrobackend.Entity.Field.UserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Getter
@Builder
@Setter
public class TokenResponse implements Serializable {

    private String accessToken;

    private String refreshToken;

    private Integer userId;
    private String fullName;
    private UserType userType;
    private long unreadNotificationCount;

    // more over
}
