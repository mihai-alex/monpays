package com.monpays.services.implementations.account;

import com.monpays.entities.account.Account;
import com.monpays.entities.account.AccountHistoryEntry;
import com.monpays.persistence.repositories.account.IAccountHistoryRepository;
import com.monpays.services.interfaces._generic.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class AccountHistoryService implements IHistoryService<AccountHistoryEntry, Account> {
    @Autowired
    private IAccountHistoryRepository accountHistoryRepository;

    @Override
    public AccountHistoryEntry addEntry(Account account) {
        AccountHistoryEntry accountHistoryEntry = new AccountHistoryEntry(account, Timestamp.from(Instant.now()));
        return accountHistoryRepository.save(accountHistoryEntry);
    }

    @Override
    public List<AccountHistoryEntry> getHistory() {
        return accountHistoryRepository.findAll();
    }

    @Override
    public List<AccountHistoryEntry> getByObject(Account account) {
        return accountHistoryRepository.findAllByAccount(account);
    }
}
