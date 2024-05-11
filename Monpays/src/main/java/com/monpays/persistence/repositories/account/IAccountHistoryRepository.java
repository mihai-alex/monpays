package com.monpays.persistence.repositories.account;

import com.monpays.entities.account.Account;
import com.monpays.entities.account.AccountHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAccountHistoryRepository extends JpaRepository<AccountHistoryEntry, Long> {
    List<AccountHistoryEntry> findAllByAccount(Account account);
}
