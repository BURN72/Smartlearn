package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAnalyticsResponse {

    private Long transactionId;
    private String studentName;
    private String courseName;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String method;
    private LocalDateTime transactionDate;
    private LocalDateTime refundDate;
}
