package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.response.PaymentHistoryResponse;
import com.example.nhatrobackend.Entity.PaymentHistory;
import com.example.nhatrobackend.Mapper.PaymentHistoryMapper;
import com.example.nhatrobackend.Responsitory.PaymentHistoryRepository;
import com.example.nhatrobackend.Service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentHistoryMapper paymentHistoryMapper;

    @Override
    public List<PaymentHistoryResponse> getPaymentHistoriesByUserId(Integer userId) {
        List<PaymentHistory> paymentHistories = paymentHistoryRepository.findByUser_UserId(userId);
        return paymentHistoryMapper.toPaymentHistoryResponses(paymentHistories);
    }
}
