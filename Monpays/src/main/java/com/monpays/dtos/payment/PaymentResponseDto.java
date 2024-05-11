package com.monpays.dtos.payment;

import com.monpays.dtos.audit.AuditEntryDto;
import com.monpays.entities.payment.enums.EPaymentStatus;
import com.monpays.entities.payment.enums.EPaymentType;
import io.micrometer.common.lang.Nullable;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class PaymentResponseDto {
    private String number;
    private Timestamp timestamp;
    private String currency;
    private Long amount;
    // the money is transferred FROM this account
    private String debitAccountNumber;
    // the money is transferred INTO this account
    private String creditAccountNumber;
    private String description;
    private EPaymentType type;
    private EPaymentStatus status;

    @Nullable
    private List<PaymentHistoryEntryDto> history;

    @Nullable
    private List<AuditEntryDto> audit;
}
