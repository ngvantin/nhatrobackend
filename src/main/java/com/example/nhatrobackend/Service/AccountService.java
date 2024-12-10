package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.Entity.Account;

public interface AccountService {
    Account findAccountByUserId(Integer userId);
}

