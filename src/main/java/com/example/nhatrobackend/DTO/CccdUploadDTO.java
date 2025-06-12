package com.example.nhatrobackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CccdUploadDTO {
    private MultipartFile frontCccd;
    private MultipartFile backCccd;
    private String fullName;
    private LocalDate dateOfBirth;
    private String address;
    private String cccdNumber;
    private String gender;
    private String nationality;
    private String hometown;
    private LocalDate cccdIssueDate;
} 