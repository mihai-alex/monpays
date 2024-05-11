package com.monpays.services.interfaces.payment;

import com.monpays.dtos.payment.PaymentRequestDto;
import com.monpays.dtos.payment.PaymentResponseDto;

import java.util.List;

public interface IPaymentService {
    PaymentResponseDto getOne(String username, String paymentNumber, boolean needsHistory, boolean needsAudit);
    List<PaymentResponseDto> getAll(String username);
    List<PaymentResponseDto> getAllByAccountNumber(String username, String accountNumber);
    PaymentResponseDto create(String username, PaymentRequestDto paymentRequestDto);
    PaymentResponseDto repair(String username, String paymentNumber, PaymentRequestDto paymentRequestDto);
    boolean approve(String username, String paymentNumber);
    boolean verify(String username, String paymentNumber);
    boolean authorize(String username, String paymentNumber);
    boolean reject(String username, String paymentNumber);

    boolean cancel(String username, String paymentNumber);
}
