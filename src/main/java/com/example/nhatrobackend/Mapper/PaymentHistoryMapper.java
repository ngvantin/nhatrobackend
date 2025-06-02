package com.example.nhatrobackend.Mapper;

import com.example.nhatrobackend.Config.MapStructConfig;
import com.example.nhatrobackend.DTO.response.PaymentHistoryResponse;
import com.example.nhatrobackend.Entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface PaymentHistoryMapper {
    @Mapping(source = "paymentId", target = "paymentId")
    @Mapping(source = "orderInfo", target = "orderInfo")
    @Mapping(source = "paymentAmount", target = "paymentAmount")
    @Mapping(source = "transactionCode", target = "transactionCode")
    @Mapping(source = "paymentTime", target = "paymentTime")
    PaymentHistoryResponse toPaymentHistoryResponse(PaymentHistory paymentHistory);

    List<PaymentHistoryResponse> toPaymentHistoryResponses(List<PaymentHistory> paymentHistories);
}
