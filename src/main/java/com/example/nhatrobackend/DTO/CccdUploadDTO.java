package com.example.nhatrobackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CccdUploadDTO {
    private MultipartFile frontCccd;
    private MultipartFile backCccd;
} 