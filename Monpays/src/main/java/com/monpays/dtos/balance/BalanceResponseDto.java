package com.monpays.dtos.balance;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class BalanceResponseDto {
    private Timestamp timestamp;
    private String accountNumber;
    private BigDecimal availableAmount;
    private BigDecimal pendingAmount;
    private BigDecimal projectedAmount;
    private BigDecimal availableCreditAmount;
    private int availableCreditCount;
    private BigDecimal availableDebitAmount;
    private int availableDebitCount;
    private BigDecimal pendingCreditAmount;
    private int pendingCreditCount;
    private BigDecimal pendingDebitAmount;
    private int pendingDebitCount;
}
