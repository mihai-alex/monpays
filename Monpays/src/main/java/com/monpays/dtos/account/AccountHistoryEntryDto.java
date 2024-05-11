package com.monpays.dtos.account;

import com.monpays.entities.account.enums.EAccountLockStatus;
import com.monpays.entities.account.enums.EAccountStatus;
import lombok.Data;

@Data
public class AccountHistoryEntryDto {
    private String accountNumber;
    private String owner;
    private String currency;
    private String name;
    private Long transactionLimit;
    private EAccountStatus status;
    private EAccountLockStatus accountLockStatus;
}
