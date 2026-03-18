package com.smartlearn.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentIntentResponse {

    private String clientSecret;

    private String paymentIntentId;

    private BigDecimal amount;

    private String currency;

    private String status;
}
