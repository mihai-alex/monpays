package com.monpays.mappers.balance;

import com.monpays.dtos.balance.BalanceResponseDto;
import com.monpays.entities.balance.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(target = "timestamp", source = "balance.timestamp")
    @Mapping(target = "accountNumber", source = "balance.account.accountNumber")
    @Mapping(target = "availableAmount", expression = "java(balance.getAvailableAmount())")
    @Mapping(target = "pendingAmount", expression = "java(balance.getPendingAmount())")
    @Mapping(target = "projectedAmount", expression = "java(balance.getProjectedAmount())")
    @Mapping(target = "availableCreditAmount", source = "balance.availableCreditAmount")
    @Mapping(target = "availableCreditCount", source = "balance.availableCreditCount")
    @Mapping(target = "availableDebitAmount", source = "balance.availableDebitAmount")
    @Mapping(target = "availableDebitCount", source = "balance.availableDebitCount")
    @Mapping(target = "pendingCreditAmount", source = "balance.pendingCreditAmount")
    @Mapping(target = "pendingCreditCount", source = "balance.pendingCreditCount")
    @Mapping(target = "pendingDebitAmount", source = "balance.pendingDebitAmount")
    @Mapping(target = "pendingDebitCount", source = "balance.pendingDebitCount")
    BalanceResponseDto toBalanceResponseDto(Balance balance);
}
