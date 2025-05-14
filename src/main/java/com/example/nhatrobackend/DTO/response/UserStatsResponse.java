package com.example.nhatrobackend.DTO.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatsResponse {
    private Long landlordCount;
    private Long tenantCount;
    private Long totalLandlordTenant;
}
