package com.monpays.services.interfaces.balance;

import com.monpays.dtos.balance.BalanceResponseDto;
import com.monpays.entities.account.Account;
import com.monpays.entities.balance.Balance;
import com.monpays.entities.payment.Payment;

import java.util.List;

public interface IBalanceService {
    List<BalanceResponseDto> getAll(String username);
    List<BalanceResponseDto> getAllByAccount(String username, String accountNumber);

    Balance classifiedGetCurrentBalance(Account account);
    void classifiedHaltPayment(Payment payment);
    void classifiedCompletePayment(Payment payment);
    void classifiedCancelPayment(Payment payment);
}
