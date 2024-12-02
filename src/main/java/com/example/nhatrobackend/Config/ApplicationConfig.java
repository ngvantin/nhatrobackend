package com.example.nhatrobackend.Config;

import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.Entity.Field.AccountStatus;
import com.example.nhatrobackend.Entity.Field.LandlordStatus;
import com.example.nhatrobackend.Entity.Field.Role;
import com.example.nhatrobackend.Entity.User;
import com.example.nhatrobackend.Responsitory.AccountRepository;
import com.example.nhatrobackend.Responsitory.UserRepository;
import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
