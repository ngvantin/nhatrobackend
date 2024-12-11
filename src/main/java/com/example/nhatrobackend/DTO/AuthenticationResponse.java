package com.example.nhatrobackend.DTO;

import com.example.nhatrobackend.Entity.Field.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    boolean authenticated;
    private String accessToken;
    private String fullName;
    private Role role;
}
