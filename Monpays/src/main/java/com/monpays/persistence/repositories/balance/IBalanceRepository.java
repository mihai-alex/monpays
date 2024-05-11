package com.monpays.persistence.repositories.balance;

import com.monpays.entities.account.Account;
import com.monpays.entities.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBalanceRepository extends JpaRepository<Balance, Long> {
    List<Balance> findAllByAccount(Account account);
}
