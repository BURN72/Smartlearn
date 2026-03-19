package com.smartlearn.demo.controller;

import com.smartlearn.demo.dto.response.StripePaymentIntentResponse;
import com.smartlearn.demo.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @PostMapping("/create-payment-intent")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<StripePaymentIntentResponse> createPaymentIntent(
            @RequestParam Long enrollmentId,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(stripeService.createPaymentIntent(enrollmentId, amount));
    }

    @GetMapping("/payment-intent/{paymentIntentId}")
    public ResponseEntity<StripePaymentIntentResponse> getPaymentIntent(
            @PathVariable String paymentIntentId) {
        return ResponseEntity.ok(stripeService.getPaymentIntent(paymentIntentId));
    }
}
