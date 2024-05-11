package com.monpays.services.interfaces.account;

import com.monpays.dtos.account.AccountPendingResponseDto;
import com.monpays.dtos.account.AccountRequestDto;
import com.monpays.dtos.account.AccountResponseDto;
import com.monpays.entities.account.Account;

import javax.naming.AuthenticationException;
import java.util.List;

public interface IAccountService {
    List<AccountResponseDto> filterAccounts(String actorUsername, String columnName, String filterValue);
    List<AccountResponseDto> getAll(String username);
    AccountResponseDto getOne(String username, String accountNumber, boolean needsPending, boolean needsHistory, boolean needsAudit);
    Account classifiedGetOne(String accountNumber);
    AccountResponseDto create(String token, AccountRequestDto accountRequestDto) throws AuthenticationException;
    AccountPendingResponseDto modify(String token, String accountNumber, AccountRequestDto accountRequestDto) throws AuthenticationException;
    boolean remove(String token, String accountNumber) throws AuthenticationException;
    boolean changeAccountStatus(String username, String accountNumber, String operationType);

    boolean approve(String actorUsername, String accountNumber);

    boolean reject(String actorUsername, String accountNumber);
}
