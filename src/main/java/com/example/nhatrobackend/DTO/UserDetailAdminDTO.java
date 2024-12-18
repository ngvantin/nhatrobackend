package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailAdminDTO {
    private Integer userId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private LandlordStatus isLandlordActivated;
    private String frontCccdUrl;
    private String backCccdUrl;
}
