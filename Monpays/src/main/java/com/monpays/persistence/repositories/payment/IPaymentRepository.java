package com.monpays.persistence.repositories.payment;

import com.monpays.entities.account.Account;
import com.monpays.entities.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IPaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByDebitAccount(Account account);
    List<Payment> findAllByCreditAccount(Account account);
    Optional<Payment> findByNumber(String number);
}
