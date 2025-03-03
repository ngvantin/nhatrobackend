package com.example.nhatrobackend.DTO.respone;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
@Getter
@Builder
public class TokenResponse implements Serializable {

    private String accessToken;

    private String refreshToken;

    private Integer userId;

    // more over
}
