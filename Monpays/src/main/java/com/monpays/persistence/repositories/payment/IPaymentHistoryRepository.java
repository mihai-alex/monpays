package com.monpays.persistence.repositories.payment;

import com.monpays.entities.payment.Payment;
import com.monpays.entities.payment.PaymentHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPaymentHistoryRepository extends JpaRepository<PaymentHistoryEntry, Long> {
    List<PaymentHistoryEntry> findAllByPayment(Payment payment);
}
