package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Entity.Field.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDTO {
    private Integer userId;
     private String userUuid;
    private String fullName;
    private String profilePicture;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private MessageStatus isOnline;
}
