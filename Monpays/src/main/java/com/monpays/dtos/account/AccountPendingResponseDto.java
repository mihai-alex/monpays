package com.monpays.dtos.account;

import com.monpays.entities.account.enums.EAccountLockStatus;
import com.monpays.entities.account.enums.EAccountStatus;
import lombok.Data;

@Data
public class AccountPendingResponseDto {
    private String accountNumber;
    private String ownerUserName;
    private String currency;
    private String name;
    private Long transactionLimit;
    private EAccountStatus status;
    private EAccountLockStatus accountLockStatus;
}
