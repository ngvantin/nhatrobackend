package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.response.PaymentHistoryResponse;

import java.util.List;

public interface PaymentHistoryService {
    List<PaymentHistoryResponse> getPaymentHistoriesByUserId(Integer userId);
}
