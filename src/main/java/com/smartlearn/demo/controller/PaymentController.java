package com.smartlearn.demo.controller;

import com.smartlearn.demo.dto.response.PaymentResponse;
import com.smartlearn.demo.entity.enums.PaymentStatus;
import com.smartlearn.demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ══ ROUTES SPÉCIFIQUES (AVANT LES ROUTES GÉNÉRIQUES) ══

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionId(@PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.getPaymentByTransactionId(transactionId));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByEnrollment(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(paymentService.getPaymentsByEnrollment(enrollmentId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable String status) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(paymentService.getPaymentsByStatus(paymentStatus));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut de paiement invalide : " + status);
        }
    }

    @PostMapping("/confirm/{transactionId}")
    public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.confirmPayment(transactionId));
    }

    @PostMapping("/fail/{transactionId}")
    public ResponseEntity<PaymentResponse> failPayment(@PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.failPayment(transactionId));
    }

    // ══ ROUTES GÉNÉRIQUES (APRÈS LES ROUTES SPÉCIFIQUES) ══

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // ══ Webhook Stripe ══

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        // TODO: Valider la signature
        // TODO: Traiter les événements Stripe (payment_intent.succeeded, etc.)

        // Pour l'instant, retourner 200 OK
        return ResponseEntity.ok("Webhook reçu");
    }
}
