package com.monpays.dtos.payment;

import io.micrometer.common.lang.Nullable;
import lombok.Data;

@Data
public class PaymentRequestDto {
    @Nullable
    private String number;
    private String currency;
    private Long amount;
    // the money is transferred FROM this account
    private String debitAccountNumber;
    // the money is transferred INTO this account
    private String creditAccountNumber;
    private String description;
}
