package com.example.nhatrobackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDepositDTO {
    private Integer depositId;
    private int postId;
    private String postUuid;
    private Integer userId;
    private String userUuid;
    private String fullName;
    private String profilePicture;
} 