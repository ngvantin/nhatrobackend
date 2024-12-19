package com.example.nhatrobackend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    private String currentPassword;
    @Size(min = 8, max = 32, message = "Giới hạn từ 8-32 ký tự")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Mật khẩu tối thiểu 01 ký tự in hoa, 01 ký tự in thường, 01 chữ số, và 01 ký tự đặc biệt"
    )
    private String newPassword;
    private String confirmNewPassword;
}

