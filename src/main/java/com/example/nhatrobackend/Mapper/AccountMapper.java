package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.OtpVerificationDTO;
import com.example.nhatrobackend.DTO.RegisterRequestDTO;
import com.example.nhatrobackend.Entity.Account;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface AccountMapper extends EntityMapper<OtpVerificationDTO, Account> {
}
