package com.smartlearn.demo.dto.response;

import com.smartlearn.demo.entity.enums.PaymentStatus;
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
public class PaymentResponse {

    private Long id;

    private BigDecimal amount;

    private String currency;

    private String method;

    private PaymentStatus status;

    private String transactionId;

    private LocalDateTime paidAt;

    private Long enrollmentId;

    private String stripePaymentIntentId;
}
