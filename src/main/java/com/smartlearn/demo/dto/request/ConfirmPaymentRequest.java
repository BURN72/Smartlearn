package com.smartlearn.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmPaymentRequest {

    @NotNull(message = "L'ID du paiement est obligatoire")
    private Long paymentId;

    @NotNull(message = "L'ID du PaymentIntent Stripe est obligatoire")
    private String stripePaymentIntentId;
}
