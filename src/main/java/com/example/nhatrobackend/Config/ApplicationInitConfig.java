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
public class ApplicationInitConfig {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            // Kiểm tra xem có tài khoản nào với vai trò ADMIN chưa
            if (accountRepository.findByRole(Role.ADMIN).isEmpty()) {
                // Tạo User tương ứng
                User adminUser = new User();
                adminUser.setFullName("Admin User");
                adminUser.setPhoneNumber("0123456789");
                adminUser.setIsLandlordActivated(LandlordStatus.NOT_REGISTERED);
                adminUser.setCreatedAt(LocalDateTime.now());

                User savedUser = userRepository.save(adminUser); // Lưu User vào database

                // Tạo Account tương ứng
                Account adminAccount = new Account();
                adminAccount.setPhoneNumber(adminUser.getPhoneNumber());
                adminAccount.setPassword(passwordEncoder.encode("admin123")); // Mật khẩu mặc định
                adminAccount.setRole(Role.ADMIN);
                adminAccount.setStatus(AccountStatus.ACTIVE);
                adminAccount.setUser(savedUser); // Liên kết với User vừa tạo

                accountRepository.save(adminAccount); // Lưu Account vào database

//                System.out.println("Tài khoản ADMIN đã được tạo: admin/admin123");
            }
        };
    }
}

