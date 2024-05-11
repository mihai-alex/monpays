package com.monpays.dtos.account;

import com.monpays.entities.account.enums.EAccountLockStatus;
import com.monpays.entities.account.enums.EAccountStatus;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Version;
import lombok.Data;

@Data
public class AccountRequestDto {
    /*
     * This class is used to get the account information from the user (client).
     */
    @Version
    private Long version;

    @Nullable
    private String accountNumber;

    private String owner; // this should be the userName
    private String currency; // this should be the currency code w/o fraction digits

    private String name;

    private Long transactionLimit;

    @Nullable
    private EAccountStatus status;

    @Nullable
    private EAccountLockStatus accountLockStatus;
}
