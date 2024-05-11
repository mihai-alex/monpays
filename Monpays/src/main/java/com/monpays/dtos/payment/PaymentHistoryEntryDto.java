package com.monpays.dtos.payment;

import com.monpays.entities.payment.enums.EPaymentStatus;
import com.monpays.entities.payment.enums.EPaymentType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaymentHistoryEntryDto {

    private String number;

    private Timestamp timestamp;

    private String currency;

    private Long amount;
    // the money is transferred FROM this account

    private String debitAccount;
    // the money is transferred INTO this account

    private String creditAccount;

    private String description;

    private EPaymentType type;

    private EPaymentStatus status;
}
