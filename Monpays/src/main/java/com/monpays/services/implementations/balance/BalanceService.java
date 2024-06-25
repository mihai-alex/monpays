package com.monpays.services.implementations.balance;

import com.monpays.dtos.balance.BalanceResponseDto;
import com.monpays.entities._generic.Operation;
import com.monpays.entities._generic.enums.EOperationType;
import com.monpays.entities.account.Account;
import com.monpays.entities.balance.Balance;
import com.monpays.entities.payment.Payment;
import com.monpays.entities.user.User;
import com.monpays.mappers.balance.BalanceMapper;
import com.monpays.persistence.repositories.balance.IBalanceRepository;
import com.monpays.services.interfaces._generic.IUserActivityService;
import com.monpays.services.interfaces.account.IAccountService;
import com.monpays.services.interfaces.balance.IBalanceService;
import com.monpays.services.interfaces.user.IUserService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class BalanceService implements IBalanceService {
    @Autowired
    private IBalanceRepository balanceRepository;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IUserService userService;
    @Autowired
    private BalanceMapper balanceMapper;
    @Autowired
    private IUserActivityService userActivityService;

    @Override
    public List<BalanceResponseDto> getAll(String username) {
        User actor = userService.getOneByUserName(username);

        userActivityService.add(actor, "listAll", Balance.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, Balance.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("You don't have the right to list balances");
        }
        return balanceRepository.findAll()
                .stream()
                .map(balanceMapper::toBalanceResponseDto)
                .toList();
    }

    @Override
    public List<BalanceResponseDto> getAllByAccount(String username, String accountNumber) {
        User actor = userService.getOneByUserName(username);

        userActivityService.add(actor, "listAllByAccount", Balance.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, Balance.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("You don't have the right to list balances");
        }

        Account account = accountService.classifiedGetOne(accountNumber);
        return balanceRepository.findAllByAccount(account)
                .stream()
                .map(balanceMapper::toBalanceResponseDto)
                .toList();
    }

    @Override
    public Balance classifiedGetCurrentBalance(Account account) {
        List<Balance> balances = balanceRepository.findAllByAccount(account);
        return balances.stream()
                .max(Comparator.comparing(Balance::getTimestamp))
                .orElseThrow();
    }

    // money is sent from credit account to debit account
    @Override
    public void classifiedHaltPayment(Payment payment) {
        Pair<Balance, Balance> balances = internalGetCreditAndDebitBalances(payment);
        Balance debitBalance = balances.getFirst();
        Balance creditBalance = balances.getSecond();

        internalAddSentAmountIntoPending(payment.getConvertedAmount(), debitBalance, creditBalance);

        balanceRepository.save(debitBalance);
        balanceRepository.save(creditBalance);
    }

    @Override
    public void classifiedCompletePayment(Payment payment) {
        Pair<Balance, Balance> balances = internalGetCreditAndDebitBalances(payment);
        Balance debitBalance = balances.getFirst();
        Balance creditBalance = balances.getSecond();

        internalRemoveSentAmountFromPending(payment.getConvertedAmount(), debitBalance, creditBalance);
        internalAddSentAmountIntoAvailable(payment.getConvertedAmount(), debitBalance, creditBalance);

        balanceRepository.save(debitBalance);
        balanceRepository.save(creditBalance);
    }

    @Override
    public void classifiedCancelPayment(Payment payment) {
        Pair<Balance, Balance> balances = internalGetCreditAndDebitBalances(payment);
        Balance debitBalance = balances.getFirst();
        Balance creditBalance = balances.getSecond();

        internalRemoveSentAmountFromPending(payment.getConvertedAmount(), debitBalance, creditBalance);

        balanceRepository.save(debitBalance);
        balanceRepository.save(creditBalance);
    }

    private Pair<Balance, Balance> internalGetCreditAndDebitBalances(Payment payment) {
        Account debitAccount = payment.getDebitAccount();
        Balance debitBalance = new Balance(classifiedGetCurrentBalance(debitAccount));

        Account creditAccount = payment.getCreditAccount();
        Balance creditBalance = new Balance(classifiedGetCurrentBalance(creditAccount));

        return Pair.of(debitBalance, creditBalance);
    }

    // money is sent from the debit balance to the credit balance
    private void internalAddSentAmountIntoPending(Long sentAmount, Balance debitBalance, Balance creditBalance) {
        debitBalance.sendAmountPending(sentAmount);
        creditBalance.receiveAmountPending(sentAmount);
    }

    private void internalRemoveSentAmountFromPending(Long amount, Balance debitBalance, Balance creditBalance) {
        debitBalance.removeSentAmountPending(amount);
        creditBalance.removeReceivedAmountPending(amount);
    }

    private void internalAddSentAmountIntoAvailable(Long amount, Balance debitBalance, Balance creditBalance) {
        debitBalance.sendAmountAvailable(amount);
        creditBalance.receiveAmountAvailable(amount);
    }
}
