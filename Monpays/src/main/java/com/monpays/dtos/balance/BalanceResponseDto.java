package com.monpays.dtos.balance;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class BalanceResponseDto {
    private Timestamp timestamp;
    private String accountNumber;
    private Long availableAmount;
    private Long pendingAmount;
    private Long projectedAmount;
    private Long availableCreditAmount;
    private int availableCreditCount;
    private Long availableDebitAmount;
    private int availableDebitCount;
    private Long pendingCreditAmount;
    private int pendingCreditCount;
    private Long pendingDebitAmount;
    private int pendingDebitCount;
}
