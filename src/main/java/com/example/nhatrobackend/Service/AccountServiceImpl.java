package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.Account;
import com.example.nhatrobackend.Responsitory.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public Account findAccountByUserId(Integer userId) {
        // Xử lý logic tìm kiếm Account
        return accountRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản cho người dùng với ID: " + userId));
    }
}

