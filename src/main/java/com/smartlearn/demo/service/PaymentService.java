package com.smartlearn.demo.service;

import com.smartlearn.demo.dto.response.PaymentResponse;
import com.smartlearn.demo.entity.Enrollment;
import com.smartlearn.demo.entity.Payment;
import com.smartlearn.demo.entity.enums.PaymentStatus;
import com.smartlearn.demo.repository.EnrollmentRepository;
import com.smartlearn.demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentService enrollmentService;

    /**
     * Créer un paiement en attente
     */
    public PaymentResponse createPayment(Long enrollmentId, BigDecimal amount, String method) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée : " + enrollmentId));

        Payment payment = Payment.builder()
                .enrollment(enrollment)
                .amount(amount)
                .currency("XAF") // Franc CFA par défaut
                .method(method)
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        return mapToResponse(saved);
    }

    /**
     * Confirmer un paiement (appelé par webhook Stripe)
     */
    public PaymentResponse confirmPayment(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + transactionId));

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setPaidAt(LocalDateTime.now());

        // Activer l'inscription
        enrollmentService.activateEnrollment(payment.getEnrollment().getId());

        Payment updated = paymentRepository.save(payment);
        return mapToResponse(updated);
    }

    /**
     * Marquer un paiement comme échoué
     */
    public PaymentResponse failPayment(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + transactionId));

        payment.setStatus(PaymentStatus.FAILED);
        Payment updated = paymentRepository.save(payment);
        return mapToResponse(updated);
    }

    /**
     * Obtenir un paiement par ID
     */
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + paymentId));
        return mapToResponse(payment);
    }

    /**
     * Obtenir les paiements d'une inscription
     */
    public List<PaymentResponse> getPaymentsByEnrollment(Long enrollmentId) {
        return paymentRepository.findByEnrollmentId(enrollmentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les paiements par statut
     */
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Rembourser un paiement
     */
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + paymentId));

        // Vérifier que c'est récent (moins de 7j)
        LocalDateTime refundDeadline = payment.getPaidAt().plusDays(7);
        if (LocalDateTime.now().isAfter(refundDeadline)) {
            throw new RuntimeException("La période de remboursement (7 jours) a expiré");
        }

        payment.setStatus(PaymentStatus.REFUNDED);

        // Rembourser l'inscription
        enrollmentService.refundEnrollment(payment.getEnrollment().getId());

        Payment updated = paymentRepository.save(payment);
        return mapToResponse(updated);
    }

    /**
     * Obtenir un paiement par transaction ID
     */
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + transactionId));
        return mapToResponse(payment);
    }

    /**
     * Mettre à jour le transaction ID (notamment pour Stripe)
     */
    public PaymentResponse updateTransactionId(Long paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé : " + paymentId));

        payment.setTransactionId(transactionId);
        Payment updated = paymentRepository.save(payment);
        return mapToResponse(updated);
    }

    /**
     * Mapper une entité Payment vers PaymentResponse
     */
    public PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .enrollmentId(payment.getEnrollment().getId())
                .build();
    }
}
