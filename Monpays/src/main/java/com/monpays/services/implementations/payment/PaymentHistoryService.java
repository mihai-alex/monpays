package com.monpays.services.implementations.payment;

import com.monpays.entities.payment.Payment;
import com.monpays.entities.payment.PaymentHistoryEntry;
import com.monpays.mappers.payment.PaymentMapper;
import com.monpays.persistence.repositories.payment.IPaymentHistoryRepository;
import com.monpays.services.interfaces.payment.IPaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentHistoryService implements IPaymentHistoryService {
    @Autowired
    private IPaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public PaymentHistoryEntry addEntry(Payment payment) {
        PaymentHistoryEntry paymentHistoryEntry = paymentMapper.toPaymentHistoryEntry(payment);
        return paymentHistoryRepository.save(paymentHistoryEntry);
    }

    @Override
    public List<PaymentHistoryEntry> getByObject(Payment payment) {
        return paymentHistoryRepository.findAllByPayment(payment);
    }
}
