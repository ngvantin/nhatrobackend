package com.example.nhatrobackend.Service;

import com.example.nhatrobackend.DTO.response.PaymentHistoryResponse;
import com.example.nhatrobackend.DTO.response.AdminPaymentHistoryResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentHistoryService {
    List<PaymentHistoryResponse> getPaymentHistoriesByUserId(Integer userId);
    List<Long> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate);
    PaymentHistoryResponse getPaymentDetailsById(Integer userId, Long paymentId);
    Page<PaymentHistoryResponse> getAllPaymentHistories(int page, int size, String sortBy, String direction);
    AdminPaymentHistoryResponse getAdminPaymentDetailsById(Long paymentId);
}
