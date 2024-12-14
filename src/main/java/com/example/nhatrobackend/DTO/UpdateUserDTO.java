package com.example.nhatrobackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String profilePicture;
    private String phoneNumber;
    private String address;
}

