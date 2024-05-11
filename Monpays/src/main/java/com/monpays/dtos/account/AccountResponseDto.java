package com.monpays.dtos.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.monpays.dtos.audit.AuditEntryDto;
import com.monpays.entities.account.enums.EAccountLockStatus;
import com.monpays.entities.account.enums.EAccountStatus;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Version;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseDto {
    @Version
    private Long version;

    private String accountNumber;
    private String owner;
    private String currency;
    private String name;
    private Long transactionLimit;
    private EAccountStatus status;
    private EAccountLockStatus accountLockStatus;

    @Nullable
    private AccountPendingResponseDto pendingEntity;

    @Nullable
    private List<AccountHistoryEntryDto> history;

    @Nullable
    private List<AuditEntryDto> audit;
}
