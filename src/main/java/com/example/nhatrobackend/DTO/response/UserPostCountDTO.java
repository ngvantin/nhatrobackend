package com.example.nhatrobackend.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPostCountDTO {
    private Integer userId;
    private String userUuid;
    private String fullName;
    private Integer postCount;
} 