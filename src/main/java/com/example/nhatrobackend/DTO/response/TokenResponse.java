package com.example.nhatrobackend.DTO.response;

import com.example.nhatrobackend.Entity.Field.UserType;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
@Getter
@Builder
public class TokenResponse implements Serializable {

    private String accessToken;

    private String refreshToken;

    private Integer userId;
    private String fullName;
    private UserType userType;

    // more over
}
