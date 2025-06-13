package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.request.DepositRequest;
import com.example.nhatrobackend.DTO.response.VNPayResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface DepositService {
    VNPayResponse createDepositPayment(DepositRequest depositRequest, HttpServletRequest request, Integer currentUserId);
    String processDepositCallback(HttpServletRequest request);
    Object getDepositDetails(Integer depositId, Integer currentUserId);
    Object confirmDeposit(Integer depositId, Integer currentUserId, Boolean isConfirmed);
} 