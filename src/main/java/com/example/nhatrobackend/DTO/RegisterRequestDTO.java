//package com.example.nhatrobackend.DTO;
//
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
//import jakarta.validation.constraints.Size;
//import lombok.Data;
//
//@Data
//public class RegisterRequestDTO {
//
//    private String fullName;
//    @Pattern(regexp = "^(0|\\+84)[35789]\\d{8}$", message = "Định dạnh số điện thoại không hợp lệ")
//    private String phoneNumber;
//    @Size(min = 8, max = 32, message = "Giới hạn từ 8-32 ký tự")
//    @Pattern(
//            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
//            message = "Mật khẩu tối thiểu 01 ký tự in hoa,01 ký tự in thường,01 chữ số."
//    )
//    private String password;
//}
