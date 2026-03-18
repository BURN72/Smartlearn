package com.smartlearn.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentRequest {

    @NotNull(message = "L'ID de l'inscription est obligatoire")
    private Long enrollmentId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount;

    @NotNull(message = "La méthode de paiement est obligatoire")
    private String method; // STRIPE, ORANGE_MONEY

    private String stripePaymentIntentId; // Pour Stripe
}
