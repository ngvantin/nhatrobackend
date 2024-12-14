package com.example.nhatrobackend.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminDTO {
    private Integer userId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private LocalDateTime updatedAt;
}
