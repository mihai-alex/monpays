package com.monpays.entities.balance;

import com.monpays.entities.account.Account;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "balances")
@Data
//@AllArgsConstructor
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp timestamp;
    @ManyToOne
    private Account account;
    // money THAT HE HAS RECEIVED
    @Column
    private BigDecimal availableCreditAmount;
    @Column
    private int availableCreditCount;
    // money THAT HE HAS SENT
    @Column
    private BigDecimal availableDebitAmount;
    @Column
    private int availableDebitCount;
    @Column
    private BigDecimal pendingCreditAmount;
    @Column
    private int pendingCreditCount;
    @Column
    private BigDecimal pendingDebitAmount;
    @Column
    private int pendingDebitCount;

    public Balance() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Balance(Balance balance) {
        this.id = null;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.account = balance.account;
        this.availableCreditAmount = balance.availableCreditAmount;
        this.availableCreditCount = balance.availableCreditCount;
        this.availableDebitAmount = balance.availableDebitAmount;
        this.availableDebitCount = balance.availableDebitCount;
        this.pendingCreditAmount = balance.pendingCreditAmount;
        this.pendingCreditCount = balance.pendingCreditCount;
        this.pendingDebitAmount = balance.pendingDebitAmount;
        this.pendingDebitCount = balance.pendingDebitCount;
    }

    // methods for getting amounts

    public BigDecimal getAvailableAmount() {              // working amount
        return availableCreditAmount.subtract(availableDebitAmount);
    }

    public BigDecimal getPendingAmount() {
        return pendingCreditAmount.subtract(pendingDebitAmount);
    }

    public BigDecimal getProjectedAmount() {
        return getAvailableAmount().add(getPendingAmount());
    }

    // methods for sending and receiving money

    public void sendAmountPending(BigDecimal amount) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        pendingDebitAmount = pendingDebitAmount.add(amount);
        pendingDebitCount++;
    }

    public void removeSentAmountPending(BigDecimal amount) throws IllegalArgumentException {
        internalVerifyAmountForRemoval(amount, pendingDebitAmount, pendingDebitCount);
        pendingDebitAmount = pendingDebitAmount.subtract(amount);
        pendingDebitCount--;
    }

    public void receiveAmountPending(BigDecimal amount) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        pendingCreditAmount = pendingCreditAmount.add(amount);
        pendingCreditCount++;
    }

    public void removeReceivedAmountPending(BigDecimal amount) throws IllegalArgumentException {
        internalVerifyAmountForRemoval(amount, pendingCreditAmount, pendingCreditCount);
        pendingCreditAmount = pendingCreditAmount.subtract(amount);
        pendingCreditCount--;
    }

    public void sendAmountAvailable(BigDecimal amount) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        availableDebitAmount = availableDebitAmount.add(amount);
        availableDebitCount++;
    }

    public void receiveAmountAvailable(BigDecimal amount) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        availableCreditAmount = availableCreditAmount.add(amount);
        availableCreditCount++;
    }

    // verification methods for sending and receiving money

    private void internalVerifyAmount(BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {                          // amount must be positive
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    private void internalVerifyAmountForRemoval(BigDecimal amount, BigDecimal actualAmount, int count) throws IllegalArgumentException {
        internalVerifyAmount(amount);
        if (amount.compareTo(actualAmount) > 0) {         // amount must be less than or equal to pending amount
            throw new IllegalArgumentException("Amount to remove must be less than or equal to actual amount");
        }
        if (count <= 0) {              // pending count must be greater than 0
            throw new IllegalArgumentException("Transaction count must be greater than 0");
        }
    }
}
