package com.example.nhatrobackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostAdminDTO {
    private int postId;
    private String title;
    private String description;
    private String fullName;  // Tên người đăng
    private LocalDateTime createdAt;
}
