package com.smartlearn.demo.repository;

import com.smartlearn.demo.entity.Payment;
import com.smartlearn.demo.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByEnrollmentId(Long enrollmentId);

    List<Payment> findByStatus(PaymentStatus status);
}
