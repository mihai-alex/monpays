package com.monpays.services.interfaces.payment;

import com.monpays.entities.payment.Payment;
import com.monpays.entities.payment.PaymentHistoryEntry;

import java.util.List;

public interface IPaymentHistoryService {
    PaymentHistoryEntry addEntry(Payment payment);
    List<PaymentHistoryEntry> getByObject(Payment payment);
}
