package com.example.nhatrobackend.Service.impl;

import com.example.nhatrobackend.DTO.response.AdminPaymentHistoryResponse;
import com.example.nhatrobackend.DTO.response.PaymentHistoryResponse;
import com.example.nhatrobackend.Entity.PaymentHistory;
import com.example.nhatrobackend.Mapper.PaymentHistoryMapper;
import com.example.nhatrobackend.Responsitory.PaymentHistoryRepository;
import com.example.nhatrobackend.Service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentHistoryMapper paymentHistoryMapper;

    @Override
    public List<PaymentHistoryResponse> getPaymentHistoriesByUserId(Integer userId) {
        List<PaymentHistory> paymentHistories = paymentHistoryRepository.findByUser_UserIdOrderByPaymentTimeDesc(userId);
        return paymentHistoryMapper.toPaymentHistoryResponses(paymentHistories);
    }

    @Override
    public List<Long> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> monthlyRevenues = paymentHistoryRepository.getMonthlyRevenue(startDate, endDate);
        
        // Tạo map để lưu doanh thu theo tháng
        Map<YearMonth, Long> revenueMap = new HashMap<>();
        
        // Lấy danh sách các tháng trong khoảng thời gian
        YearMonth currentMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        
        // Khởi tạo tất cả các tháng với doanh thu = 0
        while (!currentMonth.isAfter(endMonth)) {
            revenueMap.put(currentMonth, 0L);
            currentMonth = currentMonth.plusMonths(1);
        }
        
        // Cập nhật doanh thu cho các tháng có dữ liệu
        for (Object[] row : monthlyRevenues) {
            Integer year = (Integer) row[0];
            Integer month = (Integer) row[1];
            Long revenue = ((Number) row[2]).longValue();
            YearMonth yearMonth = YearMonth.of(year, month);
            revenueMap.put(yearMonth, revenue);
        }
        
        // Tạo list kết quả theo thứ tự thời gian
        List<Long> result = new ArrayList<>();
        currentMonth = YearMonth.from(startDate);
        while (!currentMonth.isAfter(endMonth)) {
            result.add(revenueMap.get(currentMonth));
            currentMonth = currentMonth.plusMonths(1);
        }
        
        return result;
    }

    @Override
    public PaymentHistoryResponse getPaymentDetailsById(Integer userId, Long paymentId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByPaymentIdAndUser_UserId(paymentId, userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + paymentId));
        
        return paymentHistoryMapper.toPaymentHistoryResponse(paymentHistory);
    }


    @Override
    public Page<PaymentHistoryResponse> getAllPaymentHistories(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<PaymentHistory> paymentHistories = paymentHistoryRepository.findAll(pageable);
        return paymentHistories.map(paymentHistoryMapper::toPaymentHistoryResponse);
    }

    @Override
    public AdminPaymentHistoryResponse getAdminPaymentDetailsById(Long paymentId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + paymentId));
        
        return mapToAdminPaymentHistoryResponse(paymentHistory);
    }

    private AdminPaymentHistoryResponse mapToAdminPaymentHistoryResponse(PaymentHistory paymentHistory) {
        if (paymentHistory == null) return null;
        
        AdminPaymentHistoryResponse response = new AdminPaymentHistoryResponse();
        response.setPaymentId(paymentHistory.getPaymentId());
        response.setOrderInfo(paymentHistory.getOrderInfo());
        response.setPaymentAmount(paymentHistory.getPaymentAmount());
        response.setTransactionCode(paymentHistory.getTransactionCode());
        response.setPaymentTime(paymentHistory.getPaymentTime());
        response.setResponseCode(paymentHistory.getResponseCode());
        
        // Map user information
        if (paymentHistory.getUser() != null) {
            response.setUserId(paymentHistory.getUser().getUserId());
            response.setFullName(paymentHistory.getUser().getFullName());
            response.setEmail(paymentHistory.getUser().getEmail());
            response.setPhoneNumber(paymentHistory.getUser().getPhoneNumber());
        }
        
        return response;
    }
}
