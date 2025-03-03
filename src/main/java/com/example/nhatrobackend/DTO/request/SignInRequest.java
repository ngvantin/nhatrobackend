package com.example.nhatrobackend.DTO.request;


import com.example.nhatrobackend.util.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "PhoneNumber must be not null")
    private String phoneNumber;

    @NotBlank(message = "Password must be not blank")
    private String password;

//    @NotNull(message = "username must be not null")
//    private Platform platform;
//
//    private String deviceToken;
//
//    private String version;
}