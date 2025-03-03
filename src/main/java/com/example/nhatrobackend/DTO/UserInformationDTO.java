package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInformationDTO {
    private String fullName;
    private UserType userType;
}
